package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewSearchDto {
    private String searchDateType;              // 조회 날짜
    private String searchBy;                    // 조회 유형(이름, 내용) ("title", "content")
    private String searchQuery = "";            // 검색 단어
}
