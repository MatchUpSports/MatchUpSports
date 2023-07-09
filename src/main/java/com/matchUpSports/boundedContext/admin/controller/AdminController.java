package com.matchUpSports.boundedContext.admin.controller;

import com.matchUpSports.base.rq.Rq;
import com.matchUpSports.boundedContext.admin.service.AdminService;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    private final AdminService adminService;
    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("")
    public String showAdmMain() {
        return "admin/main";
    }

    @GetMapping("/members")
    public String showMembers(Model model) {
        List<Member> members = memberService.findAll();
        model.addAttribute("members", members);
        return "admin/members";
    }

    @GetMapping("/deleteMember/{id}")
    public String deleteMember(@PathVariable Long id) {
        memberService.deleteHard(id);
        return rq.redirectWithMsg("/admin/members", "%d번 회원을 삭제하였습니다.".formatted(id));
    }
}
