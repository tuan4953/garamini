package com.garage.repair.service;

import com.garage.exception.ResourceNotFoundException;
import com.garage.repair.dto.*;
import com.garage.repair.model.*;
import com.garage.repair.repository.*;
import com.garage.service.model.Service;
import com.garage.service.repository.ServiceRepository;
import com.garage.sparepart.model.SparePart;
import com.garage.sparepart.repository.SparePartRepository;
import com.garage.user.model.Role;
import com.garage.user.model.User;
import com.garage.user.repository.UserRepository;
import com.garage.vehicle.model.Vehicle;
import com.garage.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class RepairService {

    private final RepairOrderRepository repairOrderRepository;
    private final InspectionRepository inspectionRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final SparePartRepository sparePartRepository;

    @Transactional
    public Inspection createInspection(InspectionRequest request) {
        Inspection inspection = Inspection.builder()
                .vehicle(vehicleRepository.findById(request.getVehicleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe")))
                .technician(request.getTechnicianId() != null ?
                        userRepository.findById(request.getTechnicianId()).orElse(null) : null)
                .initialCondition(request.getInitialCondition())
                .diagnosticResult(request.getDiagnosticResult())
                .build();
        return inspectionRepository.save(inspection);
    }

    @Transactional
    public RepairResponse createRepairOrder(RepairRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin phương tiện!"));

        User customer = null;
        if (request.getCustomerId() != null) {
            customer = userRepository.findById(request.getCustomerId()).orElse(vehicle.getOwner());
        } else {
            customer = vehicle.getOwner();
        }

        if (customer == null) {
            throw new ResourceNotFoundException("Không xác định được chủ sở hữu của xe!");
        }

        User technician = null;
        if (request.getTechnicianId() != null && request.getTechnicianId() > 0) {
            technician = userRepository.findById(request.getTechnicianId()).orElse(null);
        }

        String code = "RO-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));

        RepairOrder.RepairStatus initialStatus = request.getStatus() != null ? request.getStatus() : RepairOrder.RepairStatus.RECEIVING;

        RepairOrder order = RepairOrder.builder()
                .orderCode(code)
                .vehicle(vehicle)
                .customer(customer)
                .technician(technician)
                .status(initialStatus)
                .note(request.getNote())
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        if (request.getInspectionId() != null) {
            order.setInspection(inspectionRepository.findById(request.getInspectionId()).orElse(null));
        }

        if (initialStatus == RepairOrder.RepairStatus.COMPLETED) {
            order.setCompletedAt(LocalDateTime.now());
        }

        BigDecimal total = BigDecimal.ZERO;
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (RepairRequest.RepairItemRequest itemReq : request.getItems()) {
                if (itemReq != null && (itemReq.getServiceId() != null || itemReq.getSparePartId() != null || itemReq.getItemName() != null)) {
                    RepairItem item = buildRepairItem(order, itemReq);
                    order.getItems().add(item);
                    total = total.add(item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO);
                }
            }
        }
        order.setTotalAmount(total);

        RepairOrder saved = repairOrderRepository.save(order);
        return mapToResponse(saved);
    }

    @Transactional
    public RepairResponse updateRepairOrder(Long id, RepairUpdateRequest request) {
        RepairOrder order = repairOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn sửa chữa với ID: " + id));

        if (request.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phương tiện!"));
            order.setVehicle(vehicle);
            if (request.getCustomerId() == null && vehicle.getOwner() != null) {
                order.setCustomer(vehicle.getOwner());
            }
        }

        if (request.getCustomerId() != null) {
            User customer = userRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng!"));
            order.setCustomer(customer);
        }

        if (request.getTechnicianId() != null) {
            if (request.getTechnicianId() > 0) {
                User tech = userRepository.findById(request.getTechnicianId()).orElse(null);
                order.setTechnician(tech);
            } else {
                order.setTechnician(null);
            }
        }

        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
            if (request.getStatus() == RepairOrder.RepairStatus.COMPLETED) {
                if (order.getCompletedAt() == null) {
                    order.setCompletedAt(LocalDateTime.now());
                }
            } else {
                order.setCompletedAt(null);
            }
        }

        if (request.getNote() != null) {
            order.setNote(request.getNote());
        }

        if (request.getItems() != null) {
            order.getItems().clear();
            BigDecimal total = BigDecimal.ZERO;
            for (RepairRequest.RepairItemRequest itemReq : request.getItems()) {
                if (itemReq != null && (itemReq.getServiceId() != null || itemReq.getSparePartId() != null || itemReq.getItemName() != null)) {
                    RepairItem item = buildRepairItem(order, itemReq);
                    order.getItems().add(item);
                    total = total.add(item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO);
                }
            }
            order.setTotalAmount(total);
        }

        RepairOrder saved = repairOrderRepository.save(order);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteRepairOrder(Long id) {
        RepairOrder order = repairOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu sửa chữa với ID: " + id));
        repairOrderRepository.delete(order);
    }

    @Transactional(readOnly = true)
    public Page<RepairResponse> getOrdersPaged(RepairOrder.RepairStatus status, String keyword, Pageable pageable) {
        // Normalize keyword
        String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        boolean hasStatus  = (status != null);
        boolean hasKeyword = (kw != null);

        Page<RepairOrder> page;
        if (!hasStatus && !hasKeyword) {
            // Không có filter nào → trả tất cả, mới nhất lên đầu
            page = repairOrderRepository.findAll(
                    pageable.isPaged()
                            ? org.springframework.data.domain.PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))
                            : pageable);
        } else if (hasStatus && !hasKeyword) {
            // Chỉ filter status
            page = repairOrderRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else if (!hasStatus) {
            // Chỉ filter keyword
            page = repairOrderRepository.searchByKeyword(kw, pageable);
        } else {
            // Cả hai
            page = repairOrderRepository.searchByStatusAndKeyword(status, kw, pageable);
        }

        return page.map(this::mapToResponse);
    }


    @Transactional(readOnly = true)
    public RepairResponse getOrderById(Long id) {
        RepairOrder order = repairOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu sửa chữa với ID: " + id));
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getRepairStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("total", repairOrderRepository.count());

            // Nhóm "Tiếp Nhận": bao gồm RECEIVING, RECEIVED, PENDING
            List<RepairOrder.RepairStatus> receivingGroup = List.of(
                    RepairOrder.RepairStatus.RECEIVING,
                    RepairOrder.RepairStatus.RECEIVED,
                    RepairOrder.RepairStatus.PENDING
            );
            stats.put("receiving", repairOrderRepository.countByStatusIn(receivingGroup));

            // Nhóm "Chẩn Đoán": DIAGNOSING, ACCEPTED, CONFIRMED
            List<RepairOrder.RepairStatus> diagnosingGroup = List.of(
                    RepairOrder.RepairStatus.DIAGNOSING,
                    RepairOrder.RepairStatus.ACCEPTED,
                    RepairOrder.RepairStatus.CONFIRMED
            );
            stats.put("diagnosing", repairOrderRepository.countByStatusIn(diagnosingGroup));

            // Nhóm "Đang Sửa": IN_PROGRESS, PROCESSING, FIXING, ASSIGNED, WAITING_PARTS, WAITING, APPROVED
            List<RepairOrder.RepairStatus> inProgressGroup = List.of(
                    RepairOrder.RepairStatus.IN_PROGRESS,
                    RepairOrder.RepairStatus.PROCESSING,
                    RepairOrder.RepairStatus.FIXING,
                    RepairOrder.RepairStatus.ASSIGNED,
                    RepairOrder.RepairStatus.WAITING_PARTS,
                    RepairOrder.RepairStatus.WAITING,
                    RepairOrder.RepairStatus.APPROVED
            );
            stats.put("inProgress", repairOrderRepository.countByStatusIn(inProgressGroup));

            // Nhóm "Hoàn Thành": COMPLETED, FINISHED, DONE, PAID
            List<RepairOrder.RepairStatus> completedGroup = List.of(
                    RepairOrder.RepairStatus.COMPLETED,
                    RepairOrder.RepairStatus.FINISHED,
                    RepairOrder.RepairStatus.DONE,
                    RepairOrder.RepairStatus.PAID
            );
            stats.put("completed", repairOrderRepository.countByStatusIn(completedGroup));

            // Nhóm "Đã Hủy": CANCELLED, REJECTED
            List<RepairOrder.RepairStatus> cancelledGroup = List.of(
                    RepairOrder.RepairStatus.CANCELLED,
                    RepairOrder.RepairStatus.REJECTED
            );
            stats.put("cancelled", repairOrderRepository.countByStatusIn(cancelledGroup));

            // Doanh thu: tổng của tất cả nhóm hoàn thành
            BigDecimal totalRev = repairOrderRepository.sumTotalRevenueByStatusIn(completedGroup);
            stats.put("totalRevenue", totalRev != null ? totalRev : BigDecimal.ZERO);

        } catch (Exception e) {
            stats.put("total", 0L);
            stats.put("receiving", 0L);
            stats.put("diagnosing", 0L);
            stats.put("inProgress", 0L);
            stats.put("completed", 0L);
            stats.put("cancelled", 0L);
            stats.put("totalRevenue", BigDecimal.ZERO);
        }
        return stats;
    }

    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> getAllCustomers() {
        List<User> list = userRepository.findByRoleIn(List.of(Role.CUSTOMER, Role.ROLE_USER, Role.USER));
        if (list.isEmpty()) {
            return userRepository.findAll();
        }
        return list;
    }

    @Transactional(readOnly = true)
    public List<User> getAllTechnicians() {
        List<User> list = userRepository.findByRoleIn(List.of(Role.TECHNICIAN, Role.ROLE_TECHNICIAN, Role.ADMIN, Role.ROLE_ADMIN));
        if (list.isEmpty()) {
            return userRepository.findAll();
        }
        return list;
    }

    @Transactional(readOnly = true)
    public List<Service> getAllActiveServices() {
        return serviceRepository.findByActiveTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<SparePart> getAllActiveSpareParts() {
        return sparePartRepository.findByActiveTrueOrderByNameAsc();
    }

    private RepairItem buildRepairItem(RepairOrder order, RepairRequest.RepairItemRequest itemReq) {
        int qty = itemReq.getQuantity() != null && itemReq.getQuantity() > 0 ? itemReq.getQuantity() : 1;
        BigDecimal unitPrice = itemReq.getUnitPrice() != null ? itemReq.getUnitPrice() : BigDecimal.ZERO;

        Service service = null;
        SparePart sparePart = null;

        if (itemReq.getServiceId() != null && itemReq.getServiceId() > 0) {
            service = serviceRepository.findById(itemReq.getServiceId()).orElse(null);
            if (service != null && (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0)) {
                unitPrice = service.getPrice() != null ? service.getPrice() : BigDecimal.ZERO;
            }
        }

        if (itemReq.getSparePartId() != null && itemReq.getSparePartId() > 0) {
            sparePart = sparePartRepository.findById(itemReq.getSparePartId()).orElse(null);
            if (sparePart != null && (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0)) {
                unitPrice = sparePart.getPrice() != null ? sparePart.getPrice() : BigDecimal.ZERO;
            }
        }

        BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(qty));

        return RepairItem.builder()
                .repairOrder(order)
                .service(service)
                .sparePart(sparePart)
                .quantity(qty)
                .unitPrice(unitPrice)
                .totalPrice(itemTotal)
                .build();
    }

    public RepairResponse mapToResponse(RepairOrder order) {
        if (order == null) return null;

        List<RepairResponse.RepairItemResponse> itemResponses = new ArrayList<>();
        if (order.getItems() != null) {
            itemResponses = order.getItems().stream().map(i -> {
                String itemName = "Hạng mục khác";
                String type = "KHÁC";
                Long srvId = null;
                Long spId = null;

                if (i.getService() != null) {
                    itemName = i.getService().getName();
                    type = "DỊCH VỤ";
                    srvId = i.getService().getId();
                } else if (i.getSparePart() != null) {
                    itemName = i.getSparePart().getName();
                    type = "PHỤ TÙNG";
                    spId = i.getSparePart().getId();
                }

                return RepairResponse.RepairItemResponse.builder()
                        .id(i.getId())
                        .serviceId(srvId)
                        .sparePartId(spId)
                        .itemName(itemName)
                        .type(type)
                        .quantity(i.getQuantity() != null ? i.getQuantity() : 1)
                        .unitPrice(i.getUnitPrice() != null ? i.getUnitPrice() : BigDecimal.ZERO)
                        .totalPrice(i.getTotalPrice() != null ? i.getTotalPrice() : BigDecimal.ZERO)
                        .build();
            }).collect(Collectors.toList());
        }

        return RepairResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode() != null ? order.getOrderCode() : "RO-N/A")
                .vehicleId(order.getVehicle() != null ? order.getVehicle().getId() : null)
                .licensePlate(order.getVehicle() != null ? order.getVehicle().getLicensePlate() : "N/A")
                .vehicleModel(order.getVehicle() != null ? (order.getVehicle().getBrand() + " " + order.getVehicle().getModel()) : "N/A")
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerName(order.getCustomer() != null ? order.getCustomer().getFullName() : "Khách vãng lai")
                .customerPhone(order.getCustomer() != null ? order.getCustomer().getPhone() : "N/A")
                .technicianId(order.getTechnician() != null ? order.getTechnician().getId() : null)
                .technicianName(order.getTechnician() != null ? order.getTechnician().getFullName() : "Chưa phân công")
                .status(order.getStatus() != null ? order.getStatus() : RepairOrder.RepairStatus.RECEIVING)
                .totalAmount(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
                .note(order.getNote() != null ? order.getNote() : "")
                .createdAt(order.getCreatedAt())
                .completedAt(order.getCompletedAt())
                .items(itemResponses)
                .build();
    }

    public void saveDiagnosis(DiagnosisRequest request) {
        // Logic cập nhật kết quả chẩn đoán
    }
}