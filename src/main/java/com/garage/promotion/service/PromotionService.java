package com.garage.promotion.service;

import com.garage.promotion.dto.PromotionRequest;
import com.garage.promotion.model.Promotion;
import com.garage.promotion.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public List<Promotion> getActivePromotions() {
        return promotionRepository.findActivePromotions(LocalDate.now());
    }

    public List<Promotion> getAllPromotionsForAdmin() {
        return promotionRepository.findAllByOrderByCreatedAtDesc();
    }

    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã khuyến mãi có ID: " + id));
    }

    public Promotion getValidPromotionByCode(String code) {
        Promotion promotion = promotionRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalArgumentException("Mã khuyến mãi không tồn tại!"));

        if (!promotion.isValid()) {
            throw new IllegalArgumentException("Mã khuyến mãi đã hết hạn hoặc ngưng áp dụng!");
        }
        return promotion;
    }

    @Transactional
    public Promotion saveOrUpdatePromotion(PromotionRequest request) {
        Promotion promotion;
        if (request.getId() != null) {
            promotion = getPromotionById(request.getId());
            promotion.setCode(request.getCode().toUpperCase().trim());
            promotion.setTitle(request.getTitle());
            promotion.setDescription(request.getDescription());
            promotion.setDiscountPercent(request.getDiscountPercent());
            promotion.setDiscountAmount(request.getDiscountAmount());
            promotion.setStartDate(request.getStartDate());
            promotion.setEndDate(request.getEndDate());
            promotion.setActive(request.getActive());
        } else {
            if (promotionRepository.existsByCodeIgnoreCase(request.getCode())) {
                throw new IllegalArgumentException("Mã khuyến mãi này đã tồn tại trên hệ thống!");
            }
            promotion = Promotion.builder()
                    .code(request.getCode().toUpperCase().trim())
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .discountPercent(request.getDiscountPercent())
                    .discountAmount(request.getDiscountAmount())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .active(request.getActive() != null ? request.getActive() : true)
                    .build();
        }
        return promotionRepository.save(promotion);
    }

    @Transactional
    public void toggleStatus(Long id) {
        Promotion promotion = getPromotionById(id);
        promotion.setActive(!promotion.getActive());
        promotionRepository.save(promotion);
    }

    @Transactional
    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }
}