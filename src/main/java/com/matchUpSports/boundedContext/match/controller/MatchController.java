package com.matchUpSports.boundedContext.match.controller;

import com.matchUpSports.base.rsData.RsData;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchController {
    private final MatchService matchService;

    //매치 페이지의 메인 조건 보여주기
    @GetMapping("/filter")
    public String showMatchingFilter() {
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

