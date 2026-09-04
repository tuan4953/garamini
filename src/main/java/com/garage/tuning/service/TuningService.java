package com.garage.tuning.service;

import com.garage.exception.ResourceNotFoundException;
import com.garage.tuning.dto.TuningProductRequest;
import com.garage.tuning.dto.TuningProductResponse;
import com.garage.tuning.model.TuningCategory;
import com.garage.tuning.model.TuningProduct;
import com.garage.tuning.repository.TuningCategoryRepository;
import com.garage.tuning.repository.TuningProductRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class TuningService {

    private final TuningProductRepository productRepository;
    private final TuningCategoryRepository categoryRepository;

    public Page<TuningProductResponse> searchProducts(Long categoryId, Boolean active, String keyword, Pageable pageable) {
        return productRepository.searchProducts(categoryId, active, keyword, pageable).map(this::mapToResponse);
    }

    public TuningProductResponse getProductById(Long id) {
        return mapToResponse(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm độ xe")));
    }

    @Transactional
    public TuningProductResponse createProduct(TuningProductRequest request) {
        TuningCategory category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        }

        TuningProduct product = TuningProduct.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .category(category)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return mapToResponse(productRepository.save(product));
    }

    private TuningProductResponse mapToResponse(TuningProduct p) {
        return TuningProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stockQuantity(p.getStockQuantity())
                .imageUrl(p.getImageUrl())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : "Khác")
                .active(p.getActive())
                .createdAt(p.getCreatedAt())
                .build();
    }


}