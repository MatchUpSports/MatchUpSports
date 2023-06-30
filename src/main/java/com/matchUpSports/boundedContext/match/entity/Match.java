package com.matchUpSports.boundedContext.match.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
@Table(name = "match_table") // 테이블 이름 변경
@EntityListeners(AuditingEntityListener.class) // 추가이유 : createdDate Db에 생성하게함
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime applicationDate;   // 신청일

    private String matchTier;       // 매치 티어

    private LocalDate matchDate;    // 매치 날짜

    private Integer usageTime;          // 이용 시간

    private String progressStatus;  // 진행 상태 (타입에 관해서 추후에 생각해봐야 함)

//    @ManyToOne(fetch = FetchType.LAZY)
//    private Field field;                    // 시설

    //아래의 4가지 데이터는 field에서 받아와야할 데이터 입니다
    private String fieldLocation; // 구장 위치 = fieldLocation
    private String stadium; //구장 이름 = fieldName
    private int subStadiumCount; //그 구장에 있는 구장 수 = courtCount
    private int participantCount; //한 매치에 참여하는 인원수
}