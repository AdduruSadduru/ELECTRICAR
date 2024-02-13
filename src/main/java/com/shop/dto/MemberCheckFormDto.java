package com.shop.dto;

import com.shop.constant.Role;
import com.shop.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class MemberCheckFormDto {
    private Long id;
    private String name;            // 수정 불가
    private String email;           // 수정 불가
    private String address;         // 수정 불가
    private String tel;             // 수정 불가
    private Role role;              // 수정 가능
    private String createTime;

    private static ModelMapper modelMapper = new ModelMapper();

    public static MemberCheckFormDto of(Member member) {
        return modelMapper.map(member, MemberCheckFormDto.class);
    }
}
