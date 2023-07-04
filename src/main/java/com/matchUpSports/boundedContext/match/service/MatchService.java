package com.matchUpSports.boundedContext.match.service;

import com.matchUpSports.base.rsData.RsData;
import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import com.matchUpSports.boundedContext.futsalField.repository.FutsalFieldRepository;
import com.matchUpSports.boundedContext.match.entity.Match;
import com.matchUpSports.boundedContext.match.entity.MatchMember;
import com.matchUpSports.boundedContext.match.matchFormDto.MatchForm;
import com.matchUpSports.boundedContext.match.repository.MatchMemberRepository;
import com.matchUpSports.boundedContext.match.repository.MatchRepository;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {
    private final MatchRepository matchRepository;
    private final MatchMemberRepository matchMemberRepository;
    private final FutsalFieldRepository fieldRepository;
    private final MemberRepository memberRepository;

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
        for (int i = 1; i <= maxSubStadiumCount; i++) {
            if (subStadiumMembers[i] < 2) {
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
    private void addMatchMember(Member loggedInMember, Match newMatch) {
        MatchMember matchMember = new MatchMember();
        matchMember.setMember(loggedInMember);
        matchMember.setMatch(newMatch);
        matchMember.setVoteCount(0);
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
        return RsData.successOf("매치 확정에 성공했습니다.");
    }

}

