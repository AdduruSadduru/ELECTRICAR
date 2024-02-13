package com.shop.controller;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.service.ItemService;
import com.shop.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ItemService itemService;
    private final HttpSession httpSession;
    private final MemberService memberService;

    @GetMapping(value = "/")
    public String main(Model model, Principal principal) {
        Pageable pageable = PageRequest.of(0, 6);
        Page<MainItemDto> items = itemService.getMainItemPage(pageable);

        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        model.addAttribute("items", items);
        model.addAttribute("maxPage", 5);
        return "main";
    }
    @GetMapping(value = "/scroll")
    @ResponseBody
    public ResponseEntity infiniteScroll(Optional<Integer> page, Model model, Principal principal) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<MainItemDto> items = itemService.getMainItemPage(pageable);

        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        return new ResponseEntity<Page<MainItemDto>>(items, HttpStatus.OK);
    }
}
