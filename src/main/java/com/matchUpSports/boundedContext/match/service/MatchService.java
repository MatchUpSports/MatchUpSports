package com.matchUpSports.boundedContext.match.service;

import com.matchUpSports.base.rq.Rq;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

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
    private final Rq rq;

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

        //테스트를 위해서 2명에서 1명으로 수정함
        // 10명으로 늘림
        for (int i = 1; i <= maxSubStadiumCount; i++) {
            if (subStadiumMembers[i] < 10) {
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

    @Transactional
    public RsData<Match> join(int usageTime, FutsalField field, LocalDate matchDate) {
        Match match = Match.builder()
                .usageTime(usageTime)
                .matchDate(LocalDate.now())
                .field(field)
                .matchDate(matchDate)
                .progressStatus("진행")
                .build();

        matchRepository.save(match);

        return RsData.of("S-1", "매치가 만들어졌습니다.", match);
    }

    @Transactional
    public RsData<MatchMember> matching(Match match, Member member, boolean myVote, int votedCount, int team) {

        MatchMember matchMember = MatchMember.builder()
                .myVote(myVote)
                .member(member)
                .match(match)
                .votedCount(votedCount)
                .team(team)
                .build();

        matchMemberRepository.save(matchMember);

        return RsData.of("S-1", "매치에" + member.getUsername() + "이 추가되었습니다.", matchMember);
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
        Match newMatch = matchForm.toEntity();
        newMatch.setParticipantCount(1);
        newMatch.setSubStadiumCount(availableSubStadium);
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

        // 테스트 용으로 내가 확정 누르면 `ProgressStatus`를 변경 - 김진호
        if (matchMember.getMember().getUsername().equals("KAKAO__2884083653")) {
            match.setProgressStatus("1");
            matchRepository.save(match);
        }

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

    public List<MatchMember> findMVP() {
        List<MatchMember> MVP = matchMemberRepository.findMaxVotedMember();

        return MVP;
    }

    @Transactional
    public RsData<MatchVote> vote(MatchMember fromVoteMember, VoteForm voteForm) {
        MatchMember toVoteMember = matchMemberRepository.findById(voteForm.getToVote()).get();

        // 자신에게 투표하는 예외처리 해야함

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
    public void paySuccess(String matchId, String userName, int amount) {
        List<MatchMember> matchMemberList = this.getMatchMemberList(Long.valueOf(matchId));

        // 시설 관리자에게 결제한 금액 포인트 추가
        this.getMatch(Long.valueOf(matchId)).getField().getMember().receivePaidPoints(amount);

        Optional<MatchMember> optionalMatchMember = matchMemberList.stream().filter(matchMember -> matchMember.getMember().getUsername().equals(userName)).findFirst();

        optionalMatchMember.get().updateIsPaid();
    }

    @Transactional
    public void setMatchMemberTeams(List<MatchMember> matchMemberList) {
        // 리스트를 섞기 위해 랜덤 객체 생성
        Random random = new Random();

        // confirmedMembers 리스트 섞기
        Collections.shuffle(matchMemberList, random);

        // 첫 5명에게 team 1 지정
        for (int i = 0; i < 5; i++) {
            MatchMember member = matchMemberList.get(i);
            member.setTeam(1);
        }

        // 나머지 5명에게 team 2 지정
        for (int i = 5; i < 10; i++) {
            MatchMember member = matchMemberList.get(i);
            member.setTeam(2);
        }
    }

    public void sendKakaoMessage(User user, Match match) throws IOException {
        List<MatchMember> paidMatchMembers = matchMemberRepository.findAllByMatch(match);
        boolean isAllPaid = paidMatchMembers.stream().allMatch(MatchMember::isIspaid);

        if (isAllPaid) {
            // 멤버의 액세스 토큰을 저장
            String tokenValue = memberService.getAccessToken(user.getUsername());

            if (tokenValue == null) {
                // 카카오 로그인 필요하다고 토스트 메시지 같은 거 띄우면 됨
                //return rq.redirectWithMsg("/kakao/login","카카오 로그인 필요");
                System.out.println("카카오톡 토큰 에러");
            }

            //String message = user.getUsername() + "님\n" + match.getMatchDate() + "\n" + match.getFieldLocation() + " - " + match.getStadium() + "\n" + match.getUsageTime() + "타임 풋살 예약이 완료되었습니다.";
            String message = user.getUsername() + "님   " + match.getMatchDate() + "   " + match.getFieldLocation() + " - " + match.getStadium() + "   " + match.getUsageTime() + "타임 풋살 예약이 완료되었습니다.";

            // 액세스 토큰과 메시지로 REST API 요청하는 메서드
            kakaoTalkMessageService.sendTextMessage(tokenValue, message);
        }
    }

}

