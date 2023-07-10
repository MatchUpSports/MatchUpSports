package com.matchUpSports.boundedContext.admin.controller;

import com.matchUpSports.base.rq.Rq;
import com.matchUpSports.boundedContext.admin.service.AdminService;
import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import com.matchUpSports.boundedContext.futsalField.service.FutsalFieldService;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    private final AdminService adminService;
    private final MemberService memberService;
    private final FutsalFieldService futsalFieldService;
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

    @GetMapping("/fields")
    public String showField(Model model) {
        List<FutsalField> pending = futsalFieldService.getAllPendingFutsalFields();
        model.addAttribute("pending", pending);
        return "admin/fields";
    }

    @GetMapping("/approve/{id}")
    public String approveFacility(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            futsalFieldService.approveFutsalField(id);
            rq.redirectWithMsg("/admin", "Facility approved successfully.");
        } catch (Exception e) {
            rq.redirectWithMsg("/admin", "Error approving facility");
        }

        return "redirect:/admin/facilities";
    }

    @GetMapping("/deleteMember/{id}")
    public String deleteMember(@PathVariable Long id) {
        memberService.deleteHard(id);
        return rq.redirectWithMsg("/admin/members", "%d번 회원을 삭제하였습니다.".formatted(id));
    }
}
