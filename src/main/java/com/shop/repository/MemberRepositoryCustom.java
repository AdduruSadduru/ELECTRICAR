package com.shop.repository;

import com.shop.dto.MemberSearchDto;
import com.shop.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> searchMember(MemberSearchDto memberSearchDto);
}
