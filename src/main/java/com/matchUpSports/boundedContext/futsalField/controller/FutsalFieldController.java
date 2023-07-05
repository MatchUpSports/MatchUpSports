package com.matchUpSports.boundedContext.futsalField.controller;

import com.matchUpSports.base.rq.Rq;
import com.matchUpSports.boundedContext.futsalField.dto.FutsalFieldRegistrationDto;
import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import com.matchUpSports.boundedContext.futsalField.form.CreateFutsalFieldForm;
import com.matchUpSports.boundedContext.futsalField.service.FutsalFieldService;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/field")
public class FutsalFieldController {
    private final FutsalFieldService futsalFieldService;

    private final MemberService memberService;
    private final Rq rq;

//    @GetMapping("")
//    public String showManageMain(Model model) {
//        Member currentMember = rq.getMember();
//        List<FutsalField> myFields = futsalFieldService.getFutsalFieldsOfCurrentUser(currentMember);
//
//        model.addAttribute("myFields", myFields);
//        return "field/management";  // HTML 뷰의 이름
//    }

    @GetMapping("/myFields")
    public String getMyFutsalFields(Model model) {
        Member currentMember = rq.getMember();
        List<FutsalField> myFields = futsalFieldService.getFutsalFieldsOfCurrentUser(currentMember);

        model.addAttribute("myFields", myFields);
        return "field/management";  // HTML 뷰의 이름
    }

    @GetMapping("")
    public String showManageMain() {
        return "redirect:/field/myFields";
    }

    @GetMapping("/detail/{id}")
    public String showDetailFutsalField(@PathVariable Long id, Model model){

        return "field/futsalFieldDetail";
    }


    @GetMapping("/create")
    public String createField(Model model) {

        model.addAttribute("createForm", new CreateFutsalFieldForm());

        return "field/create";
    }

    @PostMapping("/create")
    public String create(@Valid FutsalFieldRegistrationDto dto) {
        Member member = memberService.findByUsername(rq.getMember().getUsername());
        FutsalField futsalField = futsalFieldService.create(member, dto);
        return rq.redirectWithMsg("/field/myFields".formatted(futsalField.getId()), "등록되었습니다");
    }

//    @PostMapping("/create")
//    public String createField(Member member, @Validated CreateFutsalFieldForm createForm, @AuthenticationPrincipal User user, BindingResult bindingResult, Model model) {
////        Member member = memberService.findByUsername(rq.getMember());
////        FutsalField futsalField = futsalFieldService.create(member, dto);
//        if (bindingResult.hasErrors()) {
//            // 유효성 검사를 실패한 경우 작성 폼으로 다시 이동
//            model.addAttribute(bindingResult);
//
//            return "field/create";
//        }
//
//        futsalFieldService.create(createForm, member);
//
//        return "/field";
//    }
}
