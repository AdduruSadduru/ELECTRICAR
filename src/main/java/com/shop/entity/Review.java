package com.shop.entity;

import com.shop.dto.ReviewDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review")
@Getter
@Setter
@ToString
public class Review extends BaseEntity{
    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy(value = "comment_id ASC")
    private List<Comment> commentList = new ArrayList<>();      // 댓글

    private String dateTime;                // 날짜 포멧용

    public static Review writeReview(ReviewDto reviewDto, Member member) {
        Review wReview = new Review();
        wReview.setId(reviewDto.getId());
        wReview.setTitle(reviewDto.getTitle());
        wReview.setContent(reviewDto.getContent());
        wReview.setMember(member);
        return wReview;
    }
    public void updateReview(ReviewDto reviewDto) {
        this.title = reviewDto.getTitle();
        this.content = reviewDto.getContent();
    }

}
