package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {
    private Long reviewId;
    private Long parentId;
    private String content;
}
