package com.matchUpSports.boundedContext.vote.entity;

import com.matchUpSports.boundedContext.match.entity.MatchMember;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private MatchMember matchMember;    // 매치 사용자

    private int voteType;               // 투표 사유

    // 그램그램처럼 투표 사유에 대한 toString 메서드 작성해야함.

}
