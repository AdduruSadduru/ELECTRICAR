package com.shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "comment")
public class Comment extends BaseTimeEntity {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;                                // 댓글 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    @JsonIgnore
    private Review review;                          // 리뷰 맵핑

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;                          // 작성자 맵핑

    @Column(length = 300, nullable = false)
    private String content;                         // 댓글 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Comment parent;                         // 대댓글(부모 댓글)

    private String upTime;                          // 날짜 포멧용

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> commentList = new ArrayList<>();      // 대댓글(자식 댓글)

    public void timeSetting() {
        this.upTime = getRegTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public Comment(){};

    public Comment (Review review, Member member, String content, Comment parent) {
        this.review = review;
        this.member = member;
        this.content = content;
        this.parent = parent;
    }

    public Long updateComment (String content) {
        this.content = content;
        return this.review.getId();
    }
}
