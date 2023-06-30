package com.matchUpSports.boundedContext.match.controller;

import com.matchUpSports.base.rsData.RsData;
import com.matchUpSports.boundedContext.field.entity.Field;
import com.matchUpSports.boundedContext.field.service.FieldService;
import com.matchUpSports.boundedContext.match.entity.Match;
import com.matchUpSports.boundedContext.match.matchFormDto.MatchForm;
import com.matchUpSports.boundedContext.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchController {
    private final MatchService matchService;
    private final FieldService fieldService;

    @GetMapping("/filter")
    public String showMatchingFilter(Model model) {
        List<Field> fields = fieldService.findAllFields();
        model.addAttribute("fields", fields);
        return "matching/filterPage";
    }

    @PostMapping
    public String addMatch(@ModelAttribute MatchForm matchForm, Model model) {
        RsData<Match> result = matchService.createMatch(matchForm);
        if (result.isSuccess()) {
            return "redirect:/match/waiting";
        } else {
            model.addAttribute("message", result.getMsg());
            return "matching/filterPage"; //
        }
    }

    //웨이팅 페이지 작업해야함
    @GetMapping("/waiting")
    public String showWaiting() {
        return "matching/waiting";
    }
}

