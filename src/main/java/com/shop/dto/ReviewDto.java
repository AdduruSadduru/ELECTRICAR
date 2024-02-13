package com.shop.dto;

import com.shop.entity.Review;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ReviewDto {
    private Long id;
    @NotBlank(message = "제목은 필수 입니다.")
    private String title;
    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;

    private static ModelMapper modelMapper = new ModelMapper();

    public Review createReview() {
        return modelMapper.map(this, Review.class);
    }

    public static ReviewDto of(Review review) {
        return modelMapper.map(review, ReviewDto.class);
    }
}
