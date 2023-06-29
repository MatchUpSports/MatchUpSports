package com.matchUpSports.boundedContext.match.controller;

import com.matchUpSports.boundedContext.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchController {
    private final MatchService matchService;

}
