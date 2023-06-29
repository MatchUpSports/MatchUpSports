package com.matchUpSports.boundedContext.match.entity;

import com.matchUpSports.boundedContext.field.entity.Field;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String matchTier;       // 매치 티어

    private LocalDate matchDate;    // 매치 날짜

    private int usageTime;          // 이용 시간

    private String progressStatus;  // 진행 상태 (타입에 관해서 추후에 생각해봐야 함)

    private LocalDateTime applicationDate;   // 신청일

    @ManyToOne(fetch = FetchType.LAZY)
    private Field field;                    // 시설
}
