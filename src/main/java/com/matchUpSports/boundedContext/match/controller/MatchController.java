package com.matchUpSports.boundedContext.match.controller;

import com.matchUpSports.base.districts.Districts;
import com.matchUpSports.base.rq.Rq;
import com.matchUpSports.base.rsData.RsData;
import com.matchUpSports.boundedContext.futsalField.service.FutsalFieldService;
import com.matchUpSports.boundedContext.match.Form.MatchForm;
import com.matchUpSports.boundedContext.match.Form.VoteForm;
import com.matchUpSports.boundedContext.match.entity.Match;
import com.matchUpSports.boundedContext.match.entity.MatchMember;
import com.matchUpSports.boundedContext.match.entity.MatchVote;
import com.matchUpSports.boundedContext.match.service.MatchService;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchController {
    private final MatchService matchService;
    private final MemberService memberService;
    private final FutsalFieldService fieldService;
    private final Rq rq;
    private final Districts districts;


    //매치를 위한 조건 페이지를 보여준다.
    @GetMapping("/filter")
    public String showMatchingFilter(@RequestParam(required = false) String fieldLocation, Model model) {
        model.addAttribute("fields", fieldService.findFieldsByLocation(fieldLocation));
        model.addAttribute("selectedLocation", fieldLocation);
        model.addAttribute("bigDistricts", districts.getBigDistricts());

        return "matching/filterPage";
    }

    //매치 생성 또는 있는 매치가 있다면 참여하는 메서드
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public String addMatch(@ModelAttribute MatchForm matchForm, Model model) {
        long memberId = rq.getMemberId();
        RsData<Match> result = matchService.createMatch(matchForm, memberId);

        if (result.isSuccess()) {
            return "redirect:/match/waiting";
        } else {
            return rq.historyBack(result.getMsg());
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String vote(@PathVariable("id") Long id, Model model) {

        Match match = matchService.getMatch(id);
        List<MatchMember> matchMemberList = matchService.getMatchMemberList(match.getId());

        model.addAttribute("match", match);
        model.addAttribute("matchMembers", matchMemberList);

        return "matches/vote";

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/vote/{id}")
    public String vote(@PathVariable("id") Long id, @AuthenticationPrincipal User user, VoteForm voteForm) {
        Match match = matchService.getMatch(id);
        List<MatchMember> matchMemberList = matchService.getMatchMemberList(match.getId());

        // 현재 투표 post 요청한 유저가 이 매치에 멤버가 아니라면 redirect
        Stream<MatchMember> matchMemberStream = matchMemberList.stream().filter(matchMember -> matchMember.getMember().getUsername().equals(user.getUsername()));

        List<MatchMember> matchMembers = matchMemberStream.collect(Collectors.toList());

        if (matchMembers.size() != 1) {
            return rq.historyBack("투표를 할 권한이 없습니다.");
        }

        // 투표를 보낸 사람
        MatchMember fromMatchMember = matchMembers.get(0);

        if (fromMatchMember.isMyVote() == true) {
            return rq.historyBack("이미 투표를 완료한 경기입니다.");
        }

        RsData<MatchVote> voteRsData = matchService.vote(fromMatchMember, voteForm);

        if (voteRsData.isFail()) {
            return rq.historyBack(voteRsData);
        }

        return rq.redirectWithMsg("/match/result/" + id, "투표 완료");
    }

    @GetMapping("/result/{id}")
    public String matchResult(@PathVariable("id") Long id, Model model) {
        Match match = matchService.getMatch(id);
        List<MatchMember> matchMVPMembers = matchService.findMVP();

        model.addAttribute("match", match);
        model.addAttribute("MVPs", matchMVPMembers);
        return "matches/matchResult";
    }

    @GetMapping("/pay/{matchId}")
    public String pay(@PathVariable Long matchId, @AuthenticationPrincipal User user, Model model) {
        Match match = matchService.getMatch(matchId);
        Member member = memberService.findByUsername(user.getUsername());

        model.addAttribute("match", match);
        model.addAttribute("member", member);

        return "payment/payment";
    }


    @GetMapping("/pay/success")
    public String paymentSuccess(
            Model model,
            @RequestParam(value = "orderId") String orderId,
            @RequestParam(value = "amount") Integer amount,
            @RequestParam(value = "paymentKey") String paymentKey,
            @AuthenticationPrincipal User user) throws Exception {

        String secretKey = "test_sk_jkYG57Eba3GwB6zqY2z3pWDOxmA1:";

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(secretKey.getBytes("UTF-8"));
        String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);

        URL url = new URL("https://api.tosspayments.com/v1/payments/" + paymentKey);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200 ? true : false;
        model.addAttribute("isSuccess", isSuccess);

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();

        // MatchId 추출
        int startIndex = orderId.indexOf("_");
        int endIndex = orderId.indexOf("_", startIndex + 1);
        String matchId = orderId.substring(startIndex + 1, endIndex);

        // 매치 멤버 결제 완료 필드 update
        matchService.paySuccess(matchId, user.getUsername(), amount, paymentKey);

        model.addAttribute("responseStr", jsonObject.toJSONString());
        System.out.println(jsonObject.toJSONString());

        model.addAttribute("method", jsonObject.get("method"));
        model.addAttribute("orderName", jsonObject.get("orderName"));

        if ((jsonObject.get("method")) != null) {
            if ((jsonObject.get("method")).equals("카드")) {
                model.addAttribute("cardNumber", ((JSONObject) jsonObject.get("card")).get("number"));
            } else if ((jsonObject.get("method")).equals("계좌이체")) {
                model.addAttribute("bank", ((JSONObject) jsonObject.get("transfer")).get("bank"));
            } else if ((jsonObject.get("method")).equals("휴대폰")) {
                model.addAttribute("customerMobilePhone", ((JSONObject) jsonObject.get("mobilePhone")).get("customerMobilePhone"));
            }
        } else {
            model.addAttribute("code", jsonObject.get("code"));
            model.addAttribute("message", jsonObject.get("message"));
        }

        return rq.redirectWithMsg("/match/ongoing/" + matchId, "시설 이용료를 지불하였습니다.");
    }

    @GetMapping("/ongoing/{matchId}")
    public String onGoing(@PathVariable Long matchId, @AuthenticationPrincipal User user, Model model) throws IOException {

        Match match = matchService.getMatch(matchId);
        List<MatchMember> matchMemberList = matchService.getMatchMemberList(match.getId());

        // 매치 멤버의 ispaid 필드를 확인해서 모두 true이면
        // 카카오톡 메시지로 해당 멤버에게 채팅으로 예약 되었다고 메시지 보내기
        matchService.sendKakaoMessage(user.getUsername(), match, "reserve");

        String paymentId = matchService.getMyPaymentId(match, user.getUsername());

        model.addAttribute("match", match);
        model.addAttribute("matchMembers", matchMemberList);
        model.addAttribute("paymentId", paymentId);

        return "matches/ongoing";
    }

    @Scheduled(cron = "0 0 17-21/1 * * ?", zone = "Asia/Seoul")
    public void cancelMatch() {
        try {
            matchService.cancelMatches();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}