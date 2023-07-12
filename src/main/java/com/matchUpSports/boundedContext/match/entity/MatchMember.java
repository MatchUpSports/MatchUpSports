package com.matchUpSports.boundedContext.match.entity;

import com.matchUpSports.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class MatchMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private boolean confirmed;

    private int votedCount;

    private boolean myVote;

    private int team;

    private boolean isPaid;

    private String paymentId;

    public MatchMember() {

    }

    public void setTeam(int team) {
        this.team = team;
    }

    public void voting() {
        this.myVote = true;
    }

    public void voted() {
        this.votedCount++;
    }
}