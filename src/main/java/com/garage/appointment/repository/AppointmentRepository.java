package com.garage.appointment.repository;


import com.garage.appointment.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByCustomerIdOrderByAppointmentDateDesc(Long customerId);

    List<Appointment> findByVehicleId(Long vehicleId);

    @Query("SELECT a FROM Appointment a WHERE " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:keyword IS NULL OR LOWER(a.vehicle.licensePlate) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.customer.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Appointment> searchAppointments(@Param("status") Appointment.AppointmentStatus status,
                                         @Param("keyword") String keyword,
                                         Pageable pageable);

    boolean existsByVehicleIdAndAppointmentDateBetweenAndStatusNot(
            Long vehicleId, LocalDateTime start, LocalDateTime end, Appointment.AppointmentStatus status);
}