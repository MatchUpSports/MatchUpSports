package com.matchUpSports.boundedContext.futsalField.controller;

import com.matchUpSports.base.rq.Rq;
import com.matchUpSports.boundedContext.futsalField.dto.FutsalFieldModifyDto;
import com.matchUpSports.boundedContext.futsalField.dto.FutsalFieldRegistrationDto;
import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import com.matchUpSports.boundedContext.futsalField.form.CreateFutsalFieldForm;
import com.matchUpSports.boundedContext.futsalField.service.FutsalFieldService;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/field")
@Slf4j
public class FutsalFieldController {
    private final FutsalFieldService futsalFieldService;

    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/myFields")
    public String getMyFutsalFields(Model model) {
        Member currentMember = rq.getMember();
        List<FutsalField> myFields = futsalFieldService.getFutsalFieldsOfCurrentUser(currentMember);
        List<FutsalField> approved = futsalFieldService.findByMemberAndApprovalStatus(currentMember);
        List<FutsalField> pending = futsalFieldService.findByMemberAndPendingStatus(currentMember);
        model.addAttribute("myFields", myFields);
        model.addAttribute("approved", approved);
        model.addAttribute("pending", pending);
        return "field/management";  // HTML 뷰의 이름
    }

    @GetMapping("")
    public String showManageMain() {
        return "redirect:/field/myFields";
    }

    @GetMapping("/detail/{id}")
    public String showDetailFutsalField(@PathVariable Long id, Model model) {
        FutsalField futsalField = futsalFieldService.findByIdAndDeleteDateIsNull(id);
        model.addAttribute("futsalField", futsalField);
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

    @GetMapping("/modify/{id}")
    public String modify(@PathVariable Long id, Model model) {
        FutsalField futsalField = futsalFieldService.findByIdAndDeleteDateIsNull(id);
        model.addAttribute("futsalField", futsalField);
        return "field/modify";
    }

    @PostMapping("/modify/{id}")
    public String modify(@PathVariable Long id, FutsalFieldModifyDto dto) {
        FutsalField futsalField = futsalFieldService.findByIdAndDeleteDateIsNull(id);

        if (dto.isSame(futsalField)) rq.historyBack("수정된 내용이 없습니다.");

        futsalFieldService.modify(futsalField, dto);
        return rq.redirectWithMsg("/field/myFields", "시설 정보가 수정하였습니다.");
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        FutsalField futsalField = futsalFieldService.findByIdAndDeleteDateIsNull(id);

        if (!futsalField.getMember().getUsername().equals(rq.getUsername())) {
            return rq.historyBack("접근 권한이 없습니다.");
        }

        futsalFieldService.delete(futsalField);
        return rq.redirectWithMsg("/field/myFields", "시설을 삭제하였습니다.");
    }

    @GetMapping("/delete/hard/{id}")
    public String deleteHard(@PathVariable Long id) {
        FutsalField futsalField = futsalFieldService.findById(id);
        futsalFieldService.deleteHard(futsalField);
        return rq.redirectWithMsg("/admin/fields", "시설을 영구 삭제하였습니다.");
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
