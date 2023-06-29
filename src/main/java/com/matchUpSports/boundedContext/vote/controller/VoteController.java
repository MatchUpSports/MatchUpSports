package com.matchUpSports.boundedContext.vote.controller;

import com.matchUpSports.boundedContext.vote.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/vote")
public class VoteController {
    private final VoteService voteService;
}
