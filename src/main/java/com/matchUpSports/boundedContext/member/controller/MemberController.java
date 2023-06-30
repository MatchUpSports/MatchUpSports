package com.matchUpSports.boundedContext.member.controller;

import com.matchUpSports.base.rq.Rq;
import com.matchUpSports.boundedContext.member.dto.JoiningForm;
import com.matchUpSports.boundedContext.member.dto.ModifyingDisplaying;
import com.matchUpSports.boundedContext.member.dto.ModifyingForm;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private static final String DOMAIN = "localhost";
    @Autowired
    Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/joiningForm")
    public String writeJoiningForm(Model model) {
        String area = rq.getMember().getArea();
        if (area != null) {
            return "redirect:/";
        }

        model.addAttribute("httpMethod", "POST");
        return "/member/joining_info_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/joiningForm")
    public String writeJoiningForm(@Valid JoiningForm joiningForm, HttpServletRequest request) {
        Member member = rq.getMember();
        memberService.createJoiningForm(joiningForm, member);

        String referer = request.getHeader("Referer");
        if (referer != null && referer.startsWith(DOMAIN)) {
            return "redirect:" + referer;
        }

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modifyForm")
    public String modifyUserInfo(Model model) {
        long memberId = rq.getMember().getId();
        ModifyingDisplaying modifyingForm = memberService.showModifyingForm(memberId);

        if (modifyingForm == null) {
            throw new RuntimeException("존재하지 않는 회원입니다.");
        }

        model.addAttribute("modifyingForm", modifyingForm);
        model.addAttribute("httpMethod", "PUT");
        return "/member/joining_info_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/modifyForm")
    public String modifyUserInfo(@Valid ModifyingForm modifyingForm, HttpServletRequest request) {
        Member member = rq.getMember();
        memberService.modify(modifyingForm, member);

        String referer = request.getHeader("Referer");
        if (referer != null && referer.startsWith(DOMAIN)) {
            return "redirect:" + referer;
        }

        return "redirect:/";
    }
}