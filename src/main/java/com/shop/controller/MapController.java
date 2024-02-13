package com.shop.controller;

import com.shop.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MapController {
    private final HttpSession httpSession;
    private final MemberService memberService;

    @GetMapping(value = "/map")
    public String whereAddress(Model model, Principal principal) {
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        return "map/address";
    }
}
