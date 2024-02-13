package com.shop.controller;

import com.shop.dto.CommentViewDto;
import com.shop.dto.ReviewDto;
import com.shop.dto.ReviewSearchDto;
import com.shop.entity.Review;
import com.shop.service.CommentService;
import com.shop.service.MemberService;
import com.shop.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ReviewController {
    private final HttpSession httpSession;
    private final MemberService memberService;
    private final ReviewService reviewService;
    private final CommentService commentService;

    @GetMapping(value = {"/review/list", "/review/list/{page}"})
    public String reviewList (Model model, Principal principal, ReviewSearchDto reviewSearchDto,
                              @PathVariable("page") Optional<Integer> page){
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        Page<Review> reviewPage = returnReviewList(reviewSearchDto);

        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("reviewSearchDto", reviewSearchDto);
        model.addAttribute("maxPage", 5);
        return "board/reviewList";
    }
    @GetMapping(value = "/review/write")
    public String reviewWrite (Model model, Principal principal) {
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        model.addAttribute("reviewDto", new ReviewDto());
        return "board/reviewWrite";
    }
    @PostMapping(value = "/review/write")
    @ResponseBody
    public ResponseEntity reviewWriteSave (@RequestBody @Valid ReviewDto reviewDto, BindingResult bindingResult,
                                           Principal principal) {
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
                sb.append("\r\n");
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        try {
            reviewService.saveReview(reviewDto, principal, httpSession);
        }catch (Exception e) {
            String error = "리뷰 작성중 에러가 발생하였습니다.";
            return new ResponseEntity<String>(error, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("저장 성공", HttpStatus.OK);
    }

    @GetMapping(value = "/review/view/{reviewId}")
    public String reviewView (@PathVariable("reviewId") Long reviewId, Model model, Principal principal){
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        try {
            Review review = reviewService.getReviewDtl(reviewId);
            review.setDateTime(review.getRegTime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
            review = commentService.timeSetting(review);
            if (!review .getCommentList().isEmpty()) {
                List<CommentViewDto> mainCommentList = commentService.mainComment(review.getId());
                List<CommentViewDto> subCommentList = commentService.subComment(review.getId());

                model.addAttribute("subCommentList", subCommentList);
                model.addAttribute("mainCommentList", mainCommentList);
            }
            model.addAttribute("review", review);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "게시글을 찾을 수 없습니다.");

            ReviewSearchDto reviewSearchDto = new ReviewSearchDto();
            Page<Review> reviewPage = returnReviewList(reviewSearchDto);
            model.addAttribute("reviewPage", reviewPage);
            model.addAttribute("reviewSearchDto", reviewSearchDto);
            model.addAttribute("maxPage", 5);

            return "board/reviewList";
        }
        return "board/reviewView";
    }

    @GetMapping(value = "/review/delete")
    public String reviewDelete(@RequestParam("reviewId") Long reviewId){
        reviewService.deleteReview(reviewId);
        return "redirect:list";
    }
    @GetMapping(value = "/review/update/{reviewId}")
    public String reviewUpdate(@PathVariable("reviewId") Long reviewId, Model model, Principal principal) {
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        try {
            ReviewDto reviewDto = reviewService.getReviewDto(reviewId);
            model.addAttribute("reviewDto", reviewDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "알 수 없는 오류가 발생 했습니다.");

            ReviewSearchDto reviewSearchDto = new ReviewSearchDto();
            Page<Review> reviewPage = returnReviewList(reviewSearchDto);
            model.addAttribute("reviewPage", reviewPage);
            model.addAttribute("reviewSearchDto", reviewSearchDto);
            model.addAttribute("maxPage", 5);

            return "board/reviewList";
        }
        return "board/reviewModi";
    }

    @PostMapping(value = "/review/update/{reviewId}")
    public String updateReviewDtl(@Valid ReviewDto reviewDto, BindingResult bindingResult,
                                  Model model, Principal principal) {
        model.addAttribute("memberName", memberService.loadMemberName(httpSession, principal));
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
                sb.append("\r\n");
            }
            model.addAttribute("errorMessage", sb.toString());

        } else {
            try {
                reviewService.updateReview(reviewDto);
            }catch (Exception e) {
                model.addAttribute("errorMessage", "리뷰 작성중 에러가 발생하였습니다.");
            }
        }

        ReviewSearchDto reviewSearchDto = new ReviewSearchDto();
        Page<Review> reviewPage = returnReviewList(reviewSearchDto);
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("reviewSearchDto", reviewSearchDto);
        model.addAttribute("maxPage", 5);

        return "board/reviewList";
    }
    private Page<Review> returnReviewList(ReviewSearchDto reviewSearchDto) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = reviewService.getReviewPage(reviewSearchDto, pageable);
        if (reviewPage.getContent() != null) {
            for (int i = 0; i < reviewPage.getContent().size(); i++) {
                reviewPage.getContent().get(i).setDateTime(reviewPage.getContent().get(i).getRegTime().format
                        (DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
            }
        }
        return reviewPage;
    }
}
