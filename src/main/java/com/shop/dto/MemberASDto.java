package com.shop.dto;

import com.shop.entity.Member;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class MemberASDto {
    private String name;
    private String email;

    @NotEmpty(message = "주소는 필수 입력 값 입니다.")
    private String address;

    @NotEmpty(message = "전화번호는 필수 입력 값 입니다.")
    private String tel;

    private static ModelMapper modelMapper = new ModelMapper();

    public static MemberASDto of(Member member) {
        return modelMapper.map(member, MemberASDto.class);
    }
}
