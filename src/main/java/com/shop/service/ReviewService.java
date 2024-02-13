package com.shop.service;

import com.shop.dto.ReviewDto;
import com.shop.dto.ReviewSearchDto;
import com.shop.entity.Member;
import com.shop.entity.Review;
import com.shop.repository.ReviewRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    public Page<Review> getReviewPage(ReviewSearchDto reviewSearchDto, Pageable pageable) {
        return reviewRepository.getReviewPage(reviewSearchDto, pageable);
    }

    public Long saveReview(ReviewDto reviewDto, Principal principal, HttpSession httpSession) throws Exception {
        Member member = memberService.lodeMember(httpSession, principal);
        Review saveReview = Review.writeReview(reviewDto, member);
        reviewRepository.save(saveReview);
        return saveReview.getId();
    }

    public Review getReviewDtl(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(EntityExistsException::new);
        return review;
    }

    public void deleteReview(Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(EntityExistsException::new);
        reviewRepository.delete(review);
    }

    public ReviewDto getReviewDto(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(EntityExistsException::new);
        return ReviewDto.of(review);
    }
    public Long updateReview(ReviewDto reviewDto) {
        Review review = reviewRepository.findById(reviewDto.getId())
                .orElseThrow(EntityExistsException::new);
        review.updateReview(reviewDto);
        return review.getId();
    }
}
