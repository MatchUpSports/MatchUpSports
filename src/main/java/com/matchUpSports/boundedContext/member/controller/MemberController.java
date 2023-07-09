package com.matchUpSports.boundedContext.member.controller;

import com.matchUpSports.base.districts.Districts;
import com.matchUpSports.base.rq.Rq;
import com.matchUpSports.boundedContext.member.dto.JoiningForm;
import com.matchUpSports.boundedContext.member.dto.ModifyingDisplaying;
import com.matchUpSports.boundedContext.member.dto.ModifyingForm;
import com.matchUpSports.boundedContext.member.dto.MyPage;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${custom.site.baseUrl}")
    private String domain;
    private final Rq rq;
    @Autowired
    private Districts districts;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myInformation")
    public String showMyPage(Model model) {
        Member member = rq.getMember();
        if (member == null) {
            return "redirect:member/login";
        }

        MyPage myPage = memberService.showMyPage(member);
        model.addAttribute("myInformation", myPage);
        return "member/my_information";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/joiningForm")
    public String writeJoiningForm(Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        boolean isModifiedTier = member.getTier() != 0;
        if (isModifiedTier) {
            return "redirect:";
        }

        model.addAttribute("httpMethod", "POST");
        model.addAttribute("bigDistricts", districts.getBigDistricts());
        model.addAttribute("smallDistricts", districts.getSmallDistricts());

        return "member/joining_info_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/joiningForm")
    public String writeJoiningForm(@Valid JoiningForm joiningForm, HttpServletRequest request) {
        Member member = rq.getMember();
        memberService.createJoiningForm(joiningForm, member);

        String referer = request.getHeader("Referer");
        if (referer != null && referer.startsWith(domain)) {
            return "redirect:" + referer;
        }

        return "redirect:";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modifyForm")
    public String modifyUserInfo(Model model) {
        Member member = rq.getMember();
        if (member.getTier() == 0) {
            return "redirect:member/joiningForm";
        }
        long memberId = member.getId();
        ModifyingDisplaying modifyingForm = memberService.showModifyingForm(memberId);

        if (modifyingForm == null) {
            throw new RuntimeException("존재하지 않는 회원입니다.");
        }

        model.addAttribute("modifyingForm", modifyingForm);
        model.addAttribute("httpMethod", "PUT");
        model.addAttribute("bigDistricts", districts.getBigDistricts());
        model.addAttribute("smallDistricts", districts.getSmallDistricts());
        return "member/joining_info_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/modifyForm")
    public String modifyUserInfo(@Valid ModifyingForm modifyingForm, HttpServletRequest request) {
        Member member = rq.getMember();
        memberService.modify(modifyingForm, member);

        String referer = request.getHeader("Referer");
        if (referer != null && referer.startsWith(domain)) {
            return "redirect:" + referer;
        }

        return "redirect:";
    }
}