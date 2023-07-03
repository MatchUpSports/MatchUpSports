package com.matchUpSports.boundedContext.match.service;

import com.matchUpSports.base.rsData.RsData;
import com.matchUpSports.boundedContext.field.entity.Field;
import com.matchUpSports.boundedContext.field.repository.FieldRepository;
import com.matchUpSports.boundedContext.match.entity.Match;
import com.matchUpSports.boundedContext.match.matchFormDto.MatchForm;
import com.matchUpSports.boundedContext.match.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {
    private final MatchRepository matchRepository;
    private final FieldRepository fieldRepository;

    @Transactional
    public RsData<Match> createMatch(MatchForm matchForm) {
        if (Objects.isNull(matchForm.getStadium()) || Objects.isNull(matchForm.getMatchDate()) || Objects.isNull(matchForm.getUsageTime())) {
            return RsData.of("F-1", "입력한 정보 중 누락된 값이 있습니다.");
        }

        List<Match> matches = matchRepository.findByStadiumAndMatchDateAndUsageTime(
                matchForm.getStadium(),
                matchForm.getMatchDate(),
                matchForm.getUsageTime()
        );

        int maxMemberCountForSubStadium = 2;
        Field selectedField = fieldRepository.findByFieldName(matchForm.getStadium());
        int maxSubStadiumCount = selectedField.getCourtCount();

        Map<Integer, Integer> subStadiumMembers = new HashMap<>();
        for (int i = 1; i <= maxSubStadiumCount; i++) {
            subStadiumMembers.put(i, 0);
        }

        for (Match existingMatch : matches) {
            int currentSubStadium = existingMatch.getSubStadiumCount();
            int currentParticipantCount = existingMatch.getParticipantCount();
            subStadiumMembers.put(currentSubStadium, currentParticipantCount);
        }

        int availableSubStadium = -1;
        for (int i = 1; i <= maxSubStadiumCount; i++) {
            if (subStadiumMembers.get(i) < maxMemberCountForSubStadium) {
                availableSubStadium = i;
                break;
            }
        }

        if (availableSubStadium == -1) {
            return RsData.of("F-1", "모든 구장이 가득 찼습니다.");
        }

        Match newMatch = matchForm.toEntity();
        newMatch.setParticipantCount(1);
        newMatch.setSubStadiumCount(availableSubStadium);

        if (!matches.isEmpty()) {
            for (Match existingMatch : matches) {
                if (existingMatch.getSubStadiumCount() == availableSubStadium) {
                    existingMatch.setParticipantCount(existingMatch.getParticipantCount() + 1);
                    matchRepository.save(existingMatch);
                    return RsData.successOf(existingMatch);
                }
            }
        }

        matchRepository.save(newMatch);
        return RsData.successOf(newMatch);
    }
}