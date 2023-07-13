package com.matchUpSports.boundedContext.match.Form;

import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import com.matchUpSports.boundedContext.match.entity.Match;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class MatchForm {
    private LocalDate matchDate; //매치 날짜
    private String fieldLocation; //매치 장소 (서울, 수원)
    private String stadium; // 시설 이름
    private Integer usageTime; // 이용시간

    private Long memberId;
    private String userEmail; // 추가된 속성: 사용자 이메일
    private FutsalField field;

    //팩토리 메서드
    public Match toEntity() {
        return Match.builder()
                .matchDate(matchDate)
                .fieldLocation(fieldLocation)
                .stadium(stadium)
                .participantCount(1)
                .usageTime(usageTime)
                .progressStatus("0")
                .field(field)
                .build();
    }

}
