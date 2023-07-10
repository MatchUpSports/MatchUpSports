package com.matchUpSports.boundedContext.match.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
public class MatchVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private MatchMember fromVoteMember;     // 매치에서 투표를 한 사람
    private String fromVoteMemberUsername;

    @ManyToOne(fetch = FetchType.LAZY)
    private MatchMember toVoteMember;       // 매치에서 투표를 받은 사람
    private String toVoteMemberUsername;

    private int voteTypeCode;               // 투표 사유

    public MatchVote() {
    }

    public String getVoteTypeDisplayName() {
        return switch (voteTypeCode) {
            case 1 -> "패스 굿";
            case 2 -> "슈팅 굿";
            case 3 -> "스피드 굿";
            default -> "드리블 굿";
        };
    }

    // 그램그램처럼 투표 사유에 대한 toString 메서드 작성해야함.

}
