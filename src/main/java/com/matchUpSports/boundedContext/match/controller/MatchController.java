package com.matchUpSports.boundedContext.match.controller;

import com.matchUpSports.base.rq.Rq;
import com.matchUpSports.base.rsData.RsData;
import com.matchUpSports.boundedContext.futsalField.service.FutsalFieldService;
import com.matchUpSports.boundedContext.match.entity.Match;
import com.matchUpSports.boundedContext.match.matchFormDto.MatchForm;
import com.matchUpSports.boundedContext.match.VoteForm.VoteForm;
import com.matchUpSports.boundedContext.match.entity.MatchMember;
import com.matchUpSports.boundedContext.match.service.MatchService;
import com.matchUpSports.boundedContext.member.service.MemberService;
import com.matchUpSports.boundedContext.match.entity.MatchVote;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.*;

import java.util.List;


import java.util.stream.Collectors;
import java.util.stream.Stream;


@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchController {
    private final MatchService matchService;
    private final FutsalFieldService fieldService;
    private final Rq rq;

    //매치를 위한 조건 페이지를 보여준다.
    @GetMapping("/filter")
    public String showMatchingFilter(@RequestParam(required = false) String fieldLocation, Model model) {
        model.addAttribute("fields", fieldService.findFieldsByLocation(fieldLocation));
        model.addAttribute("selectedLocation", fieldLocation);
        return "matching/filterPage";
    }

    //매치 생성및 있는 매치가 있다면 참여하는 메서드
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

    //현재 매치를 기다리는 모든 부분을 보여주도록
    @GetMapping("/waiting")
    public String showWaiting(Model model) {
        long memberId = rq.getMemberId(); // 현재 사용자의 memberId를 가져옵니다.

        List<Match> matches = matchService.getMatchesForUser(memberId);
        model.addAttribute("matches", matches);

        return "matching/waiting";
    }

    //매치를 취소하는 메서드_하드 딜리트 방식
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

    //진호님 부분 ==> 확정하기 버튼누르면 결제+카카오톡 관련 메서드 실행
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
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String vote(@PathVariable("id") Long id, Model model){

        Match match = matchService.getMatch(id);
        List<MatchMember> matchMemberList = matchService.getMatchMemberList(match.getId());

        model.addAttribute("match", match);
        model.addAttribute("matchMembers", matchMemberList);

        return "matches/vote";

    }

    //@PreAuthorize("isAuthenticated()")
    @PostMapping("/vote/{id}")
    public String vote(@PathVariable("id") Long id, @AuthenticationPrincipal User user, VoteForm voteForm){
        Match match = matchService.getMatch(id);
        List<MatchMember> matchMemberList = matchService.getMatchMemberList(match.getId());

        // 현재 투표 post 요청한 유저가 이 매치에 멤버가 아니라면 redirect
        Stream<MatchMember> matchMemberStream = matchMemberList.stream().filter(matchMember -> matchMember.getMember().getUsername().equals(user.getUsername()));

        List<MatchMember> matchMembers = matchMemberStream.collect(Collectors.toList());

        if (matchMembers.size() != 1){
            return rq.historyBack("투표를 할 권한이 없습니다.");
        }

        // 투표를 보낸 사람
        MatchMember fromMatchMember = matchMembers.get(0);

        if(fromMatchMember.isMyVote() == true){
            return rq.historyBack("이미 투표를 완료한 경기입니다.");
        }

        RsData<MatchVote> voteRsData = matchService.vote(fromMatchMember, voteForm);

        if (voteRsData.isFail()) {
            return rq.historyBack(voteRsData);
        }
        /*  매치가 없는 매치이면 전 페이지로 이동
        if (match == null){
            return "";
        }*/

        return rq.redirectWithMsg("matches/matchResult/" + id, "투표 완료");
    }

    @GetMapping("/result/{id}")
    public String matchResult(@PathVariable("id") Long id, Model model){
        Match match = matchService.getMatch(id);
        List<MatchMember> matchMVPMembers = matchService.findMVP();

        model.addAttribute("match", match);
        model.addAttribute("MVPs", matchMVPMembers);
        return "matches/matchResult";
    }
}
