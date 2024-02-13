package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.CartService;
import com.shop.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;
    private final HttpSession httpSession;

    @GetMapping(value = "/new")
    public String memberForm(Model model) {
        model.addAttribute("memberName", "로그인 해 주세요");
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/new")
    public String memberForm(@Valid MemberFormDto memberFormDto,
                             BindingResult bindingResult, Model model) {
        model.addAttribute("memberName", "로그인 해 주세요");
        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }
        Member member = Member.createMember(memberFormDto, passwordEncoder);
        memberService.saveMember(member);
        cartService.saveCart(member);
        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginMember(Model model) {
        model.addAttribute("memberName", "로그인 해 주세요");
        return "member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("memberName", "로그인 해 주세요");
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해 주세요");
        return "member/memberLoginForm";
    }

    @PostMapping(value = "/mailCheck")
    @ResponseBody
    public ResponseEntity mailCheck(@RequestBody @Valid MemberFormDto memberFormDto,
                                    BindingResult bindingResult) {
        if (bindingResult.getFieldError("email") != null) {
            String errMsg = bindingResult.getFieldError("email").getDefaultMessage();
            return new ResponseEntity<String>(errMsg, HttpStatus.BAD_REQUEST);
        }
        try {
            memberService.mailCheck(memberFormDto);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Integer>(1, HttpStatus.OK);
    }

}
