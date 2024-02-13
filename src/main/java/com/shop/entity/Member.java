package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberASDto;
import com.shop.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;
    private String address;
    private String tel;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Member() {
    }
    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        member.setPassword(passwordEncoder.encode(memberFormDto.getPassword()));
        member.setTel(memberFormDto.getTel());
        member.setRole(Role.USER);
        return member;
    }

    public Member(String name, String email) {
        this.name = name;
        this.email = email;
        this.role = Role.USER;
    }

    public Member update(String name) {
        this.name = name;
        return this;
    }
    public void updateRole(Role role) {
        this.role = role;
    }
    public void memberAS(MemberASDto memberASDto) {
        this.address = memberASDto.getAddress();
        this.tel = memberASDto.getTel();
    }
}
