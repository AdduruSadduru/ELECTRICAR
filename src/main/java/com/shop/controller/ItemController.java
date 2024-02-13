package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;
import com.shop.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final HttpSession httpSession;
    private final MemberService memberService;


    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model, Principal principal) {
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                          Principal principal) {
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {      // 안정성을 위해서 넣은 null인듯
            model.addAttribute("errorMessage", "첫 번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }
        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model, Principal principal) {
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             Model model, Principal principal) {
        String email = memberService.loadMemberEmail(httpSession, principal);
        if (bindingResult.hasErrors()) {        // 유효성 검사
            return "item/itemForm";
        }
        // 이미지 파일 첫번째 파일 넣었는지 확인
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫 번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }
        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/";        // 다시 실행 /
    }

    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page,
                             Model model, Principal principal) {
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        if (items.getContent() != null) {
            for (int i = 0; i < items.getContent().size(); i++) {
                items.getContent().get(i).setDateTime(items.getContent().get(i).getRegTime().format
                        (DateTimeFormatter.ofPattern("yyyy/MM/dd a hh:mm:ss")));
            }
        }
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        return "item/itemMng";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId, Principal principal) {
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }

    @GetMapping(value = "/item/cartItem/{cartItemId}")
    public String cartItemDtl(Model model, @PathVariable("cartItemId") Long cartItemId, Principal principal) {
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        ItemFormDto itemFormDto = itemService.cartItemDtl(cartItemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }
}

































