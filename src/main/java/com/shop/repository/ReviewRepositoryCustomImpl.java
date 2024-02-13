package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.dto.ReviewSearchDto;
import com.shop.entity.QReview;
import com.shop.entity.Review;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {
    private JPAQueryFactory queryFactory;

    public ReviewRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now();       // 현재시간 추출해서 변수에 대입

        if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        } else {
            return null;
        }
        return QReview.review.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.equals("title", searchBy)) {
            return QReview.review.title.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("content", searchBy)) {
            return QReview.review.content.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("createdBy", searchBy)) {
            return QReview.review.createdBy.like("%" + searchQuery + "%");
        }else {
            return null;
        }
    }

    @Override
    public Page<Review> getReviewPage(ReviewSearchDto reviewSearchDto, Pageable pageable) {
        QueryResults<Review> results = queryFactory.selectFrom(QReview.review)
                .where(regDtsAfter(reviewSearchDto.getSearchDateType()),
                        searchByLike(reviewSearchDto.getSearchBy(), reviewSearchDto.getSearchQuery())
                ).orderBy(QReview.review.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Review> reviewList = results.getResults();
        long total = results.getTotal();
        return new PageImpl<Review>(reviewList, pageable, total);
    }
}
