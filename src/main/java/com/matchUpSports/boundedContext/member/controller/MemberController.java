package com.matchUpSports.boundedContext.member.controller;

import com.matchUpSports.base.districts.Districts;
import com.matchUpSports.base.rq.Rq;
import com.matchUpSports.base.rsData.RsData;
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
    private final Rq rq;
    @Autowired
    private Districts districts;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myInformation")
    public String showMyPage(Model model) {
        Member member = rq.getMember();
        if (member == null) {
            return rq.redirectWithMsg("/member/login", "존재하지 않는 회원입니다");
        }

        if (member.getTier() == 0) {
            return rq.redirectWithMsg("/member/joiningForm", "필수 회원정보를 입력하여 주십시오");
        }

        MyPage myPage = memberService.showMyPage(member);
        model.addAttribute("myInformation", myPage);
        return "member/my_information";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/joiningForm")
    public String writeJoiningForm(Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member.getTier() != 0) {
            return rq.redirectWithMsg("/", "올바르지 않은 접근입니다");
        }

        model.addAttribute("httpMethod", "POST");
        model.addAttribute("bigDistricts", districts.getBigDistricts());
        model.addAttribute("smallDistricts", districts.getSmallDistricts());
        model.addAttribute("nickname", member.getNickname());

        return "member/joining_info_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/joiningForm")
    public String writeJoiningForm(@Valid JoiningForm joiningForm) {
        Member member = rq.getMember();
        RsData<Member> creatingResult = memberService.createJoiningForm(joiningForm, member);

        if (creatingResult.isFail()) {
            return rq.redirectWithMsg("/joiningForm", creatingResult.getMsg());
        }

        return rq.redirectWithMsg("/", "정보 제출 완료");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modifyForm")
    public String modifyUserInfo(Model model) {
        Member member = rq.getMember();
        if (member.getTier() == 0) {
            return rq.redirectWithMsg("/member/joiningForm", "최초 회원정보를 입력 페이지로 이동합니다");
        }
        long memberId = member.getId();
        RsData<ModifyingDisplaying> modifyingForm = memberService.showModifyingForm(memberId);

        if (modifyingForm.isFail()) {
            return rq.redirectWithMsg("/", modifyingForm.getMsg());
        }

        model.addAttribute("modifyingForm", modifyingForm.getData());
        model.addAttribute("httpMethod", "PUT");
        model.addAttribute("bigDistricts", districts.getBigDistricts());
        model.addAttribute("smallDistricts", districts.getSmallDistricts());
        return "member/joining_info_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/modifyForm")
    public String modifyUserInfo(@Valid ModifyingForm modifyingForm) {
        Member member = rq.getMember();
        RsData<Member> modifyingResult = memberService.modify(modifyingForm, member);

        if (modifyingResult.isFail()) {
            return rq.redirectWithMsg("/member/modifyForm", modifyingResult.getMsg());
        }

        return rq.redirectWithMsg("/", "정보 수정 완료");
    }
}