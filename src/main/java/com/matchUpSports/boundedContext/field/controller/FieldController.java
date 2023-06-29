package com.matchUpSports.boundedContext.field.controller;

import com.matchUpSports.boundedContext.field.form.CreateFieldForm;
import com.matchUpSports.boundedContext.field.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/field")
public class FieldController {
    private final FieldService fieldService;

    @GetMapping("/create")
    public String createField(Model model) {

        model.addAttribute("createForm", new CreateFieldForm());

        return "field/create";
    }

    @PostMapping("/create")
    public String createField(@Validated CreateFieldForm createForm, @AuthenticationPrincipal User user, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            // 유효성 검사를 실패한 경우 작성 폼으로 다시 이동
            model.addAttribute(bindingResult);

            return "field/create";
        }

        fieldService.create(createForm);

        return "matches/matchResult";
    }
}
