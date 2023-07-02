package com.matchUpSports.boundedContext.match.controller;

import com.matchUpSports.base.rsData.RsData;
import com.matchUpSports.boundedContext.field.service.FieldService;
import com.matchUpSports.boundedContext.match.entity.Match;
import com.matchUpSports.boundedContext.match.matchFormDto.MatchForm;
import com.matchUpSports.boundedContext.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchController {
    private final MatchService matchService;
    private final FieldService fieldService;

    @GetMapping("/filter")
    public String showMatchingFilter(@RequestParam(required = false) String fieldLocation, Model model) {
        model.addAttribute("fields", fieldService.findFieldsByLocation(fieldLocation));
        model.addAttribute("selectedLocation", fieldLocation);
        return "matching/filterPage";
    }

    @PostMapping
    public String addMatch(@ModelAttribute MatchForm matchForm, Model model) {
        RsData<Match> result = matchService.createMatch(matchForm);
        if (result.isSuccess()) {
            return "redirect:/match/waiting";
        } else {
            model.addAttribute("message", result.getMsg());
            return "matching/filterPage";
        }
    }

    //웨이팅 페이지 작업해야함
    @GetMapping("/waiting")
    public String showWaiting() {
        return "matching/waiting";
    }
}