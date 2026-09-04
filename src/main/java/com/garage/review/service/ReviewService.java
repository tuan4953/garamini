package com.garage.review.service;


import com.garage.review.dto.ReviewRequest;
import com.garage.review.model.Review;
import com.garage.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<Review> getAllVisibleReviews() {
        return reviewRepository.findByVisibleTrueOrderByCreatedAtDesc();
    }

    public List<Review> getAllReviewsForAdmin() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }

    public Double getAverageRating() {
        return reviewRepository.getAverageRating();
    }

    @Transactional
    public Review saveReview(ReviewRequest request) {
        Review review = Review.builder()
                .invoiceId(request.getInvoiceId())
                .customerName(request.getCustomerName())
                .rating(request.getRating())
                .comment(request.getComment())
                .visible(true)
                .build();
        return reviewRepository.save(review);
    }

    @Transactional
    public void replyToReview(Long id, String adminReply) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá có ID: " + id));
        review.setAdminReply(adminReply);
        reviewRepository.save(review);
    }

    @Transactional
    public void toggleVisibility(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá có ID: " + id));
        review.setVisible(!review.getVisible());
        reviewRepository.save(review);
    }
}