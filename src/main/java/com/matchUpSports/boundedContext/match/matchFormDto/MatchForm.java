package com.matchUpSports.boundedContext.match.matchFormDto;

import com.matchUpSports.boundedContext.match.entity.Match;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class MatchForm {
    private LocalDate matchDate;//매치 날짜
    private String fieldLocation;//매치 장소 ==> 서울, 수원으로 일단은
    private String stadium;// 시설 이름
    private Integer usageTime; // 이용시간

    //팩토리 메서드
    public Match toEntity() {
        return Match.builder()
                .matchDate(matchDate)
                .fieldLocation(fieldLocation)
                .stadium(stadium)
                .usageTime(usageTime)
                .progressStatus("0")
                .build();
    }
}