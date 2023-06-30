package com.matchUpSports.boundedContext.match.service;

import com.matchUpSports.base.rsData.RsData;
import com.matchUpSports.boundedContext.match.entity.Match;
import com.matchUpSports.boundedContext.match.matchFormDto.MatchForm;
import com.matchUpSports.boundedContext.match.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {
    private final MatchRepository matchRepository;

    @Transactional
    public RsData<Match> createMatch(MatchForm matchForm) {
        if (Objects.isNull(matchForm.getStadium()) || Objects.isNull(matchForm.getMatchDate()) || Objects.isNull(matchForm.getUsageTime())) {
            return RsData.of("F-1", "입력한 정보 중 누락된 값이 있습니다.");
        }

        // 사용자가 선택한 구장, 날짜, 시간으로 이미 등록된 매치 찾기
        List<Match> matches = matchRepository.findByStadiumAndMatchDateAndUsageTime(
                matchForm.getStadium(),
                matchForm.getMatchDate(),
                matchForm.getUsageTime()
        );

        int maxMemberCountForSubStadium = 2;
        int maxSubStadiumCount = 2;

        // 등록된 매치가 있는 경우
        if (!matches.isEmpty()) {
            for (Match existingMatch : matches) {
                // subStadium의 인원이 가득 차지 않았으면 기존 매치에 참여
                if (existingMatch.getParticipantCount() < maxMemberCountForSubStadium) {
                    existingMatch.setParticipantCount(existingMatch.getParticipantCount() + 1);

                    // 인원이 가득 찼으면 progressStatus를 업데이트
                    if (existingMatch.getParticipantCount() == maxMemberCountForSubStadium) {
                        existingMatch.setProgressStatus("1");
                    }

                    matchRepository.save(existingMatch);
                    return RsData.successOf(existingMatch);
                }
            }

            // 모든 subStadium이 가득 차 있다면 새로운 매치 생성
            if (matches.size() < maxSubStadiumCount) {
                Match match = matchForm.toEntity();
                match.setSubStadiumCount(matches.get(0).getSubStadiumCount() + 1);
                match.setParticipantCount(1);

                matchRepository.save(match);
                return RsData.successOf(match);
            } else {
                // 모든 subStadium이 가득 찼을 때 에러 메시지 반환
                return RsData.of("F-1", "모든 구장이 가득 찼습니다.");
            }
        }

        // 등록된 매치가 없는 경우, 새로운 매치를 생성하고 저장
        Match match = matchForm.toEntity();
        match.setSubStadiumCount(1);
        match.setParticipantCount(1);

        matchRepository.save(match);
        return RsData.successOf(match);
    }
}
