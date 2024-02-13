package com.shop.service;

import com.shop.constant.Role;
import com.shop.dto.*;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional                  // 트랜잭션 설정 : 성공하면 적용, 실패하면 롤백
@RequiredArgsConstructor        // final 또는 @NonNull 명령어가 붙으면 객체를 자동으로 붙여줍니다.
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;

    public Member saveMember(Member member) {
        return memberRepository.save(member);   // 테이터베이스에 저장하라는 명령
    }
    private void validateDuplicationMember(MemberFormDto memberFormDto) {
        Member findMember = memberRepository.findByEmail(memberFormDto.getEmail());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new UsernameNotFoundException(email);
        }
        // 빌더 패턴
        return User.builder().username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
    public String loadMemberName(HttpSession httpSession, Principal principal) {
        if (principal != null) {
            String email = loadMemberEmail(httpSession, principal);
            return "'" + memberRepository.findByEmail(email).getName() + "'님 환영합니다";
        } else {
            return "로그인 해 주세요";
        }
    }
    public String loadMemberEmail(HttpSession httpSession, Principal principal) {
        if (httpSession.getAttribute("Google_User") != null) {
            return ((SessionUser)httpSession.getAttribute("Google_User")).getEmail();
        } else if (httpSession.getAttribute("Naver_User") != null) {
            return ((SessionUser)httpSession.getAttribute("Naver_User")).getEmail();
        } else if (httpSession.getAttribute("Kakao_User") != null) {
            return ((SessionUser)httpSession.getAttribute("Kakao_User")).getEmail();
        } else {
            return principal.getName();
        }
    }

    public Member lodeMember(HttpSession httpSession, Principal principal) {
        String email = loadMemberEmail(httpSession, principal);
        return memberRepository.findByEmail(email);
    }

    public void mailCheck(MemberFormDto memberFormDto) {
        validateDuplicationMember(memberFormDto);
    }

    public List<MemberCheckFormDto> findMember(MemberSearchDto memberSearchDto) {
        List<Member> memberList = memberRepository.searchMember(memberSearchDto);
        List<MemberCheckFormDto> memberCheckFormDtoList = new ArrayList<>();
        for (Member member : memberList) {
            MemberCheckFormDto memberCheckFormDto = MemberCheckFormDto.of(member);
            memberCheckFormDto.setCreateTime(member.getRegTime().format
                    (DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
            memberCheckFormDtoList.add(memberCheckFormDto);
        }
        return memberCheckFormDtoList;
    }

    public String changeRoleMember(String email, Role role) {
        Member member = memberRepository.findByEmail(email);
        member.updateRole(role);
        return member.getName();
    }
    public void memberAS(MemberASDto memberASDto) {
        Member member = memberRepository.findByEmail(memberASDto.getEmail());
        member.memberAS(memberASDto);
    }
}
