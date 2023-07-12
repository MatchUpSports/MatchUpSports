package com.matchUpSports.boundedContext.match.entity;

import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@DynamicUpdate // 변경감지를 해도 전체가 다 들어가는데 변경된 부분만 부분적으로 업데이트 할 수 있도록 할 수 있다.
@OptimisticLocking(type = OptimisticLockType.DIRTY) // 변경 감지를 할 때 한번 더 체크를 한다.
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

    private Integer usageTime;          // 이용 시간 ===> 몇시부터 몇시까지 할지 적어놔야함

    private String progressStatus;  // 진행 상태 (타입에 관해서 추후에 생각해봐야 함)

    private LocalDateTime matchDateTime;

    private boolean isPaid;         // 매치가 확정되고, 매치 멤버들이 모두 돈을 지불했는지

    //아래의 4가지 데이터는 field에서 받아와서 저장하는 데이터
    private String fieldLocation; // 구장 위치 = fieldLocation
    private String stadium; //구장 이름 = fieldName
    private int subStadiumCount; //그 구장에 있는 구장 수 = courtCount
    private int participantCount; //한 매치에 참여하는 인원수

    @ManyToOne(fetch = FetchType.LAZY)
    private FutsalField field;                    // 시설

    public LocalTime getUsageTime() {
        return switch (usageTime) {
            case 1 -> LocalTime.of(17, 0, 0);
            case 2 -> LocalTime.of(18, 0, 0);
            case 3 -> LocalTime.of(19, 0, 0);
            case 4 -> LocalTime.of(20, 0, 0);
            case 5 -> LocalTime.of(21, 0, 0);
            default -> LocalTime.of(0, 0, 0);
        };
    }
}
