package com.shop.controller;

import com.shop.dto.MemberASDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private final MemberService memberService;
    private final HttpSession httpSession;

    @GetMapping(value = "/myInfo")
    public String memberAS(Model model, Principal principal) {
        Member member = memberService.lodeMember(httpSession, principal);
        MemberASDto memberASDto = MemberASDto.of(member);
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        model.addAttribute("memberASDto", memberASDto);
        return "member/memberAS";
    }

    @PostMapping(value = "/myInfo")
    public String memberUpdate(@Valid MemberASDto memberASDto, BindingResult bindingResult,
                               Model model, Principal principal) {
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        if (bindingResult.hasErrors()) {
            return "member/memberAS";
        }
        memberService.memberAS(memberASDto);
        return "redirect:/";
    }
}
