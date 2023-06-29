package com.matchUpSports.boundedContext.match.entity;

import com.matchUpSports.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MatchMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private int voteCount;

}
