package com.matchUpSports.boundedContext.adm.controller;

import com.matchUpSports.base.rq.Rq;
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

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/adm")
@Slf4j
public class AdmController {
    private final MemberService memberService;
    private final FutsalFieldService futsalFieldService;
    private final Rq rq;

    @GetMapping("")
    public String showAdmMain() {
        return "adm/main";
    }

    @GetMapping("/members")
    public String showMembers(Model model) {
        List<Member> members = memberService.findAll();
        model.addAttribute("members", members);
        return "adm/members";
    }

    @GetMapping("/fields")
    public String showField(Model model) {
        List<FutsalField> fields = futsalFieldService.findAll();
        List<FutsalField> pending = futsalFieldService.getAllPendingFutsalFields();
        List<FutsalField> approved = futsalFieldService.getAllApprovedFutsalFields();
        model.addAttribute("fields", fields);
        model.addAttribute("approved", approved);
        model.addAttribute("pending", pending);
        return "adm/fields";
    }

    @GetMapping("/approve/{id}")
    public String approveFacility(@PathVariable Long id) {
        try {
            futsalFieldService.approveFutsalField(id);
            rq.redirectWithMsg("/admin/fields", "%d번 시설을 승인하였습니다.".formatted(id));
        } catch (Exception e) {
            rq.redirectWithMsg("/admin/fields", "%d번 시설을 승인 실패하였습니다.".formatted(id));
        }
        futsalFieldService.approveFutsalField(id);

        return "redirect:/adm/fields";
    }

    @GetMapping("/deleteMember/{id}")
    public String deleteMember(@PathVariable Long id) {
        memberService.deleteHard(id);
        return rq.redirectWithMsg("/adm/members", "%d번 회원을 삭제하였습니다.".formatted(id));
    }
}
