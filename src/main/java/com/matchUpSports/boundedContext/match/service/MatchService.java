package com.matchUpSports.boundedContext.match.service;

import com.matchUpSports.base.rsData.RsData;
import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import com.matchUpSports.boundedContext.futsalField.repository.FutsalFieldRepository;
import com.matchUpSports.boundedContext.kakao.KakaoTalkMessageService;
import com.matchUpSports.boundedContext.match.Form.MatchForm;
import com.matchUpSports.boundedContext.match.Form.VoteForm;
import com.matchUpSports.boundedContext.match.entity.Match;
import com.matchUpSports.boundedContext.match.entity.MatchMember;
import com.matchUpSports.boundedContext.match.entity.MatchVote;
import com.matchUpSports.boundedContext.match.repository.MatchMemberRepository;
import com.matchUpSports.boundedContext.match.repository.MatchRepository;
import com.matchUpSports.boundedContext.match.repository.MatchVoteRepository;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.repository.MemberRepository;
import com.matchUpSports.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {
    private final MatchRepository matchRepository;
    private final MatchMemberRepository matchMemberRepository;
    private final FutsalFieldRepository fieldRepository;
    private final MemberRepository memberRepository;
    private final MatchVoteRepository matchVoteRepository;
    private final MemberService memberService;
    private final KakaoTalkMessageService kakaoTalkMessageService;

    // 매치 생성 메서드
    @Transactional
    public RsData<Match> createMatch(MatchForm matchForm, long loggedInMemberId) {

        // 로그인 한 회원 조회
        Member loggedInMember = getLoggedInMember(loggedInMemberId);
        if (loggedInMember == null) {
            return RsData.of("F-1", "멤버를 찾을 수 없습니다.");
        }

        // 이미 등록한 동일한 조건의 매치 확인
        if (isDuplicateMatch(matchForm, loggedInMember)) {
            return RsData.of("F-3", "이미 동일한 조건의 매치를 등록했습니다.");
        }

        // 입력한 정보 중 누락된 값 확인
        if (hasMissingValues(matchForm)) {
            return RsData.of("F-2", "입력한 정보 중 누락된 값이 있습니다.");
        }

        // 동일한 조건의 매치가 존재하고 인원이 2명 미만인 경우, 해당 매치에 멤버 추가
        // 동일한 조건의 매치가 모두 2명이 참가하고 있거나 존재하지 않을 경우, 새로운 매치 생성
        MatchAndSubStadium availableMatchAndSubStadium = findAvailableMatchAndSubStadium(matchForm);
        if (availableMatchAndSubStadium.subStadium == -1) {
            return RsData.of("F-1", "모든 구장이 가득 찼습니다.");
        }

        Match newOrExistingMatch;
        if (availableMatchAndSubStadium.match != null) {
            newOrExistingMatch = availableMatchAndSubStadium.match;
            newOrExistingMatch.setParticipantCount(newOrExistingMatch.getParticipantCount() + 1);
        } else {
            newOrExistingMatch = createAndSaveMatch(matchForm, availableMatchAndSubStadium.subStadium);
        }

        // 매치에 멤버 추가 및 저장
        addMatchMember(loggedInMember, newOrExistingMatch);

        return RsData.successOf(newOrExistingMatch);
    }

    //매치 경기장과 서브 경기장(=field부분에 courtCount)
    private static class MatchAndSubStadium {
        Match match;
        int subStadium;

        MatchAndSubStadium(Match match, int subStadium) {
            this.match = match;
            this.subStadium = subStadium;
        }
    }

    // 사용 가능한 매치 및 서브 스타디움 찾기 (내부 사용)
    private MatchAndSubStadium findAvailableMatchAndSubStadium(MatchForm matchForm) {
        List<Match> matches = matchRepository.findByStadiumAndMatchDateAndUsageTime(
                matchForm.getStadium(),
                matchForm.getMatchDate(),
                matchForm.getUsageTime()
        );

        FutsalField selectedField = fieldRepository.findByFieldName(matchForm.getStadium());
        int maxSubStadiumCount = selectedField.getCourtCount();

        int[] subStadiumMembers = new int[maxSubStadiumCount + 1];

        for (Match existingMatch : matches) {
            int currentSubStadium = existingMatch.getSubStadiumCount();
            int currentParticipantCount = existingMatch.getParticipantCount();
            subStadiumMembers[currentSubStadium] = currentParticipantCount;
        }

        for (int i = 1; i <= maxSubStadiumCount; i++) {
            if (subStadiumMembers[i] < 12) {
                for (Match existingMatch : matches) {
                    if (existingMatch.getSubStadiumCount() == i) {
                        return new MatchAndSubStadium(existingMatch, i);
                    }
                }
                return new MatchAndSubStadium(null, i);
            }
        }

        return new MatchAndSubStadium(null, -1);
    }

    public List<MatchMember> getMatchMemberList(Long matchId) {
        List<MatchMember> matchMemberList = matchMemberRepository.findAllByMatchId(matchId);

        return matchMemberList;
    }

    public Match getMatch(Long id) {
        Optional<Match> match = matchRepository.findById(id);

        // 존재하지 않는 매치의 id로 입력받은 경우 예외처리
        if (!match.isPresent()) throw new NoSuchElementException("해당 매치는 존재하지 않는 매치입니다.");

        return match.get();
    }

    // 로그인한 사용자 가져오기 (내부 사용)
    private Member getLoggedInMember(long memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }

    // 중복 매치 확인 (내부 사용)
    private boolean isDuplicateMatch(MatchForm matchForm, Member loggedInMember) {
        List<MatchMember> matchMembers = matchMemberRepository.findByMember(loggedInMember);

        for (MatchMember matchMember : matchMembers) {
            Match existingMatch = matchMember.getMatch();
            if (existingMatch.getStadium().equals(matchForm.getStadium()) &&
                    existingMatch.getMatchDate().equals(matchForm.getMatchDate()) &&
                    existingMatch.getUsageTime().equals(matchForm.getUsageTime())) {
                return true;
            }
        }
        return false;
    }

    // 누락된 값 확인 (내부 사용)
    private boolean hasMissingValues(MatchForm matchForm) {
        return Objects.isNull(matchForm.getStadium()) || Objects.isNull(matchForm.getMatchDate()) || Objects.isNull(matchForm.getUsageTime());
    }

    // 새로운 매치 생성 및 저장 (내부 사용)
    private Match createAndSaveMatch(MatchForm matchForm, int availableSubStadium) {

        FutsalField field = fieldRepository.findByFieldName(matchForm.getStadium());

        Match newMatch = matchForm.toEntity();
        newMatch.setParticipantCount(1);
        newMatch.setSubStadiumCount(availableSubStadium);
        newMatch.setField(field);
        newMatch.setMatchDateTime(LocalDateTime.of(newMatch.getMatchDate(), newMatch.getUsageTime()));
        matchRepository.save(newMatch);
        return newMatch;
    }

    // 매치에 멤버 추가 (내부 사용)
    public void addMatchMember(Member loggedInMember, Match newMatch) {
        MatchMember matchMember = new MatchMember();
        matchMember.setMember(loggedInMember);
        matchMember.setMatch(newMatch);
        matchMember.setVotedCount(0);
        matchMemberRepository.save(matchMember);
    }

    //대기화면에서 보여주는 메서드
    @Transactional(readOnly = true)
    public List<Match> getMatchesForUser(long memberId) {
        List<MatchMember> matchMembers = matchMemberRepository.findByMemberId(memberId);
        List<Match> matches = new ArrayList<>();
        for (MatchMember matchMember : matchMembers) {
            matches.add(matchMember.getMatch());
        }
        return matches;
    }

    // 매치 취소 메서드
    @Transactional
    public RsData<String> cancelMatch(long memberId, long matchId) {
        // Member와 Match를 찾아옵니다.
        Member member = memberRepository.findById(memberId).orElse(null);
        Match match = matchRepository.findById(matchId).orElse(null);
        if (member == null || match == null) {
            return RsData.of("F-1", "멤버 또는 매치를 찾을 수 없습니다.");
        }

        // MatchMember를 찾아옵니다.
        MatchMember matchMember = matchMemberRepository.findByMemberAndMatch(member, match);
        if (matchMember == null) {
            return RsData.of("F-2", "해당 매치에 참가하고 있지 않습니다.");
        }

        // MatchMember를 삭제하고 참가자 수를 갱신합니다.
        matchMemberRepository.delete(matchMember);
        match.setParticipantCount(match.getParticipantCount() - 1);
        matchRepository.save(match);

        // 매치 취소 성공
        return RsData.successOf("매치 취소에 성공했습니다.");
    }

    // 매치 확정을 위한 메서드 (토스 결제 및 카카오톡 메시지 전송)
    @Transactional
    public RsData<String> confirmMatch(long memberId, long matchId) {
        // Member와 Match를 찾아옵니다.
        Member member = memberRepository.findById(memberId).orElse(null);
        Match match = matchRepository.findById(matchId).orElse(null);
        if (member == null || match == null) {
            return RsData.of("F-1", "멤버 또는 매치를 찾을 수 없습니다.");
        }

        // MatchMember를 찾아옵니다.
        MatchMember matchMember = matchMemberRepository.findByMemberAndMatch(member, match);
        if (matchMember == null) {
            return RsData.of("F-2", "해당 매치에 참가하고 있지 않습니다.");
        }

        // 해당 참가자의 확정 상태를 업데이트합니다.
        matchMember.setConfirmed(true);
        matchMemberRepository.save(matchMember);

        // 모든 참가자의 확정 여부를 확인합니다.
        List<MatchMember> confirmedMembers = matchMemberRepository.findAllByMatch(match);
        boolean isAllConfirmed = confirmedMembers.stream().allMatch(MatchMember::isConfirmed);

        // 모든 참가자가 확정되었을 경우에만 `ProgressStatus`를 변경합니다.
        if (isAllConfirmed) {
            match.setProgressStatus("1");
            matchRepository.save(match);
        }

        // 랜덤으로 1팀 또는 2팀으로 나누기
        this.setMatchMemberTeams(confirmedMembers);

        return RsData.successOf("매치 확정에 성공했습니다.");
    }

    public String getMyPaymentId(Match match, String username) {
        Optional<Member> member = memberRepository.findByUsername(username);

        String paymentId = matchMemberRepository.findByMemberAndMatch(member.get(), match).getPaymentId();

        return paymentId;
    }

    public List<MatchMember> findMVP() {
        List<MatchMember> MVP = matchMemberRepository.findMaxVotedMember();

        return MVP;
    }

    @Transactional
    public RsData<MatchVote> vote(MatchMember fromVoteMember, VoteForm voteForm) {
        MatchMember toVoteMember = matchMemberRepository.findById(voteForm.getToVote()).get();

        if (fromVoteMember.getId().equals(toVoteMember.getId())) {
            return RsData.of("F-1", "자신에게 투표는 할 수 없습니다.");
        }

        MatchVote matchVote = MatchVote.builder()
                .fromVoteMember(fromVoteMember)
                .toVoteMember(toVoteMember)
                .voteTypeCode(voteForm.getVoteTypeCode())
                .build();
        matchVoteRepository.save(matchVote);

        fromVoteMember.voting();

        toVoteMember.voted();

        return RsData.of("S-1", "투표를 완료했습니다.", matchVote);
    }

    @Transactional
    public void paySuccess(String matchId, String userName, int amount, String paymentId) {
        List<MatchMember> matchMemberList = this.getMatchMemberList(Long.valueOf(matchId));

        // 시설 관리자에게 결제한 금액 포인트 추가
        this.getMatch(Long.valueOf(matchId)).getField().getMember().receivePaidPoints(amount);

        Optional<MatchMember> optionalMatchMember = matchMemberList.stream().filter(matchMember -> matchMember.getMember().getUsername().equals(userName)).findFirst();

        optionalMatchMember.get().setPaid(true);
        optionalMatchMember.get().setPaymentId(paymentId);
    }

    @Transactional
    public void setMatchMemberTeams(List<MatchMember> matchMemberList) {
        // 리스트를 섞기 위해 랜덤 객체 생성
        Random random = new Random();

        // confirmedMembers 리스트 섞기
        Collections.shuffle(matchMemberList, random);

        int half = matchMemberList.size() / 2;

        // 절반에게 team 1 지정
        for (int i = 0; i < half; i++) {
            MatchMember member = matchMemberList.get(i);
            member.setTeam(1);
        }

        // 나머지 절반에 team 2 지정
        for (int i = half; i < matchMemberList.size(); i++) {
            MatchMember member = matchMemberList.get(i);
            member.setTeam(2);
        }
    }

    @Transactional
    public void sendKakaoMessage(String userName, Match match, String purpose) throws IOException {
        String message;
        List<MatchMember> paidMatchMembers = matchMemberRepository.findAllByMatch(match);

        long count = paidMatchMembers.stream().filter(matchMember -> matchMember.isPaid()).count();

        // 멤버의 액세스 토큰을 추출
        String tokenValue = memberService.getAccessToken(userName);

        // 액세스 토큰이 없거나 만료되었으면 다시 카카오 로그인 요청
        if (tokenValue == null || memberService.checkAccessTokenIsExpired(userName)) {
            System.out.println("카카오 메시지 보내기 에러 로그인 필요 ");
        } else {
            if (count > 9 && purpose.equals("reserve")) { // 예약 메시지
                match.setPaid(true);

                message = userName + "님 " + match.getMatchDate() + " " + match.getFieldLocation() + " - " + match.getStadium() + " " + match.getUsageTime() + "타임 풋살 예약이 완료되었습니다.";

                // 액세스 토큰과 메시지로 REST API 요청하는 메서드
                kakaoTalkMessageService.sendTextMessage(tokenValue, message);
            } else if (purpose.equals("cancel")) { // 취소 메시지
                match.setPaid(false);

                message = userName + "님 " + match.getMatchDate() + " " + match.getFieldLocation() + " - " + match.getStadium() + " " + match.getUsageTime() + "타임 풋살 예약이 취소되었고, 해당 매치에 대한 결제도 취소 되었습니다.";

                kakaoTalkMessageService.sendTextMessage(tokenValue, message);
            }
        }
    }

    @Transactional
    public void cancelMatches() throws IOException, InterruptedException {
        // 결제가 완료되지 않은 match들 찾기
        List<Long> notPaidMatchIds = this.findNotPaidMatchIds();

        // 해당 MatchMember들의 결제를 다 취소하고 삭제해야 할 MatchMember 반환
        List<MatchMember> deleteMatchMemberList = this.cancelMatchPayments(notPaidMatchIds);

        // 카카오톡으로 취소 메시지 보내기
        this.sendCanceledMessage(deleteMatchMemberList);

        // match 멤버 데이터 삭제
        // match 데이터 삭제
        this.deleteMatchMembersAndMatches(deleteMatchMemberList, notPaidMatchIds);
    }

    public void deleteMatchMembersAndMatches(List<MatchMember> deleteMatchMemberList, List<Long> notPaidMatchIds) {
        matchMemberRepository.deleteAll(deleteMatchMemberList);
        matchRepository.deleteAllById(notPaidMatchIds);
    }

    public void sendCanceledMessage(List<MatchMember> matchMemberList) throws IOException {
        for (MatchMember mm : matchMemberList) {
            String userName = mm.getMember().getUsername();
            Match match = mm.getMatch();
            this.sendKakaoMessage(userName, match, "cancel");
        }
    }

    @Transactional
    public List<Long> findNotPaidMatchIds() {
        // 24시간 전에 결제가 완료되지 않은 매치리스트의 id 추출
        Stream<Long> matchIdStream = matchRepository.findAll()
                .stream()
                .filter(match -> match.getMatchDateTime().minusDays(1L).isBefore(LocalDateTime.now()) && match.isPaid() == false)
                .map(match -> match.getId());

        List<Long> matchList = matchIdStream.collect(Collectors.toList());

        System.out.println("김진호- 결제가 완료되지 않은 매치리스트의 id 추출: " + matchList);

        return matchList;
    }

    @Transactional
    public List<MatchMember> cancelMatchPayments(List<Long> notPaidMatchIds) throws IOException, InterruptedException {
        // 결제 취소를 위한 paymentId 리스트
        List<String> matchMemberPaymentIdList = new ArrayList<>();

        // 매치 취소에 의한 데이터 변경을 위한 matchMember 리스트
        List<MatchMember> matchMemberList = new ArrayList<>();

        for (Long id : notPaidMatchIds) {
            Stream<String> paymentIdStream = matchMemberRepository.findAllByMatchId(id)
                    .stream()
                    .filter(matchMember -> matchMember.isPaid())
                    .map(matchMember -> matchMember.getPaymentId());

            List<String> paymentIdList = paymentIdStream.collect(Collectors.toList());

            // 시설 관리자에게 환불된 금액만큼 포인트 회수
            Member fieldOwner = matchRepository.findById(id).get().getField().getMember();
            int fieldPrice = matchRepository.findById(id).get().getField().getPrice();
            int count = paymentIdList.size();
            int point = fieldOwner.getPoint();
            fieldOwner.setPoint(point - (count * (fieldPrice / 10)));

            List<MatchMember> matchMembers = matchMemberRepository.findAllByMatchId(id);

            matchMemberList.addAll(matchMembers);
            matchMemberPaymentIdList.addAll(paymentIdList);
        }

        // paymentId 하나씩 결제 취소 rest API 호출
        for (String paymentId : matchMemberPaymentIdList) {
            cancelPayment(paymentId);
        }


        return matchMemberList;
    }

    // 토스페이먼츠 결제 취소 REST API 호출 메서드 실행
    public void cancelPayment(String paymentId) throws IOException, InterruptedException {

        // 멱등키: 동일한 요청이 여러 번 전송될 때 중복 요청으로 처리되지 않도록 보장하는 고유한 식별자
        String idempotencyKey = UUID.randomUUID().toString();

        String secretKey = "test_sk_jkYG57Eba3GwB6zqY2z3pWDOxmA1:";

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(secretKey.getBytes("UTF-8"));
        String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/payments/" + paymentId + "/cancel"))
                .header("Authorization", authorizations)
                .header("Content-Type", "application/json")
                .header("Idempotency-Key", idempotencyKey)
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"cancelReason\":\"인원 부족으로 매치 취소\"}"))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

//        // 결제 취소 응답에서 환불 가격 추출
//        int startIdx = response.body().indexOf("\"cancelAmount\":") + 15;
//        int endIdx = response.body().indexOf(",", startIdx);
//        String amount = response.body().substring(startIdx, endIdx);

        System.out.println("토스 페이먼츠 결제 취소 결과: " + response.body());
    }

}

