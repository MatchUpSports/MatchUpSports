package com.matchUpSports.boundedContext.match.controller;

import com.matchUpSports.base.rq.Rq;
import com.matchUpSports.base.rsData.RsData;
import com.matchUpSports.boundedContext.field.service.FieldService;
import com.matchUpSports.boundedContext.match.entity.Match;
import com.matchUpSports.boundedContext.match.matchFormDto.MatchForm;
import com.matchUpSports.boundedContext.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchController {
    private final MatchService matchService;
    private final FieldService fieldService;
    private final Rq rq;

    @GetMapping("/filter")
    public String showMatchingFilter(@RequestParam(required = false) String fieldLocation, Model model) {
        model.addAttribute("fields", fieldService.findFieldsByLocation(fieldLocation));
        model.addAttribute("selectedLocation", fieldLocation);
        return "matching/filterPage";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public String addMatch(@ModelAttribute MatchForm matchForm, Model model) {
        long memberId = rq.getMemberId();
        RsData<Match> result = matchService.createMatch(matchForm, memberId);

        if (result.isSuccess()) {
            return "redirect:/match/waiting";
        } else {
            model.addAttribute("message", result.getMsg());
            return "matching/filterPage";
        }
    }

    //웨이팅 페이지 작업해야함
    @GetMapping("/waiting")
    public String showWaiting(Model model) {
        long memberId = rq.getMemberId(); // 현재 사용자의 memberId를 가져옵니다.

        List<Match> matches = matchService.getMatchesForUser(memberId);
        model.addAttribute("matches", matches);

        return "matching/waiting";
    }

    //매치에 관해서 삭제하는 메서드_하드 딜리트 방식
    @PostMapping("/cancel")
    public String cancelMatch(@RequestParam long matchId, Model model) {
        long memberId = rq.getMemberId();
        RsData<String> result = matchService.cancelMatch(memberId, matchId);

        if (result.isSuccess()) {
            return "redirect:/match/waiting";
        } else {
            model.addAttribute("message", result.getMsg());
            return "matching/waiting";
        }
    }

    @PostMapping("/confirm")
    public String confirmMatch(@RequestParam long matchId, Model model) {
        long memberId = rq.getMemberId();
        RsData<String> result = matchService.confirmMatch(memberId, matchId);

        if (result.isSuccess()) {
            return "redirect:/match/waiting";
        } else {
            model.addAttribute("message", result.getMsg());
            return "matching/waiting";
        }
    }

}
