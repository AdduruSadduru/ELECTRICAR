package com.shop.service;

import com.shop.dto.CommentDto;
import com.shop.dto.CommentViewDto;
import com.shop.entity.Comment;
import com.shop.entity.Member;
import com.shop.entity.Review;
import com.shop.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final MemberService memberService;
    private final ReviewService reviewService;
    private final CommentRepository commentRepository;

    public Review timeSetting(Review review) {
        for (Comment comment : review.getCommentList()) {
            comment.timeSetting();
        }
        return review;
    }

    public Comment saveComment (CommentDto commentDto, HttpSession httpSession, Principal principal) {
        Member member = memberService.lodeMember(httpSession, principal);
        Review review = reviewService.getReviewDtl(commentDto.getReviewId());
        Comment parent = null;
        if (commentDto.getParentId() != null) {
                parent = commentRepository.findById(commentDto.getParentId())
                    .orElseThrow(EntityNotFoundException::new);
        }
        Comment comment = new Comment(review, member, commentDto.getContent(), parent);
        return commentRepository.save(comment);
    }

    public List<CommentViewDto> mainComment (Long reviewId) {
        List<Comment> commentList = commentRepository.findByReviewIdAndParentNullOrderByIdAsc(reviewId);
        List<CommentViewDto> commentViewDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentViewDto commentViewDto = CommentViewDto.of(comment);
            commentViewDtoList.add(commentViewDto);
        }
        return commentViewDtoList;
    }

    public List<CommentViewDto> subComment (Long reviewId) {
        List<Comment> commentList = commentRepository.findByReviewIdAndParentNotNullOrderByIdAsc(reviewId);
        List<CommentViewDto> commentViewDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentViewDto commentViewDto = CommentViewDto.of(comment);
            commentViewDtoList.add(commentViewDto);
        }
        return commentViewDtoList;
    }
    public Comment commentChoice (Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(EntityNotFoundException::new);
    }
    public Long commentDelete (Comment comment) {
        Long reviewId = comment.getReview().getId();
        commentRepository.delete(comment);
        return reviewId;
    }
    public Long commentUpdate (Comment comment, String content) {
        return comment.updateComment(content);
    }

}
