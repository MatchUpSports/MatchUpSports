package com.matchUpSports.boundedContext.futsalField.controller;

import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import com.matchUpSports.boundedContext.futsalField.form.CreateFutsalFieldForm;
import com.matchUpSports.boundedContext.futsalField.service.FutsalFieldService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/field")
public class FutsalFieldController {
    private final FutsalFieldService futsalFieldService;

    @GetMapping("")
    public String showManageMain(Model model) {
        List<FutsalField> futsalFields = futsalFieldService.findAll();
        model.addAttribute("futsalFields", futsalFields);
        return "field/management";
    }

    @GetMapping("/")
    public String showAdmMain2() {
        return "redirect:/field";
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
    public String createField(@Validated CreateFutsalFieldForm createForm, @AuthenticationPrincipal User user, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            // 유효성 검사를 실패한 경우 작성 폼으로 다시 이동
            model.addAttribute(bindingResult);

            return "field/create";
        }

        futsalFieldService.create(createForm);

        return "matches/matchResult";
    }
}
