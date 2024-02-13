package com.shop.repository;

import com.shop.dto.ReviewSearchDto;
import com.shop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {
    Page<Review> getReviewPage(ReviewSearchDto reviewSearchDto, Pageable pageable);
}
