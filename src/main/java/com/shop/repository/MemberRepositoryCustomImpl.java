package com.shop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.Role;
import com.shop.dto.MemberSearchDto;
import com.shop.entity.Member;
import com.shop.entity.QMember;
import jakarta.persistence.EntityManager;
import org.thymeleaf.util.StringUtils;

import java.util.List;

public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private JPAQueryFactory queryFactory;       // 동적 쿼리 사용하기 위해 JPAQueryFactory 변수 선언

    // 생성자
    public MemberRepositoryCustomImpl(EntityManager em) {
        // JPAQueryFactory 실질적인 객체가 만들어 집니다.
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchMemberRole(Role searchRole) {
        return searchRole == null ? null : QMember.member.role.eq(searchRole);
    }
    private BooleanExpression searchQuery(String searchBy, String searchQuery) {
        if (StringUtils.equals("email", searchBy)) {
            return QMember.member.email.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("name", searchBy)) {
            return QMember.member.name.like("%" + searchQuery + "%");
        } else {
            return null;
        }
    }

    @Override
    public List<Member> searchMember(MemberSearchDto memberSearchDto) {
        List<Member> resultList = queryFactory.selectFrom(QMember.member)
                .where(searchMemberRole(memberSearchDto.getSearchRole()),
                        searchQuery(memberSearchDto.getSearchBy(), memberSearchDto.getSearchQuery()))
                .orderBy(QMember.member.id.asc()).fetch();
        return resultList;
    }
}
