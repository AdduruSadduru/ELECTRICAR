package com.shop.controller;

import com.shop.dto.MemberCheckFormDto;
import com.shop.dto.MemberSearchDto;
import com.shop.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ManagerController {
    private final HttpSession httpSession;
    private final MemberService memberService;

    @GetMapping(value = "/admin/memberCheck")
    public String memberInfo(MemberSearchDto memberSearchDto, Model model, Principal principal) {
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        List<MemberCheckFormDto> memberList = memberService.findMember(memberSearchDto);
        model.addAttribute("memberList", memberList);
        model.addAttribute("memberSearchDto", memberSearchDto);
        return "manager/memberCheck";
    }

    @PostMapping(value = "/admin/memberCheck")
    @ResponseBody
    public ResponseEntity memberInfoChange(@RequestBody MemberCheckFormDto memberCheckFormDto) {
        System.out.println(memberCheckFormDto.getEmail());
        System.out.println(memberCheckFormDto.getRole());
        String name = "";
        try {
            name = memberService.changeRoleMember(memberCheckFormDto.getEmail(), memberCheckFormDto.getRole());
        }catch (Exception e) {
            return new ResponseEntity<String>("사용자 권한설정중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("\'" + name + "\' 권한 변경 완료", HttpStatus.BAD_REQUEST);
    }
}
