package com.matchUpSports.boundedContext.match.repository;

import com.matchUpSports.boundedContext.match.entity.Match;
import com.matchUpSports.boundedContext.match.entity.MatchMember;
import com.matchUpSports.boundedContext.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchMemberRepository extends JpaRepository<MatchMember, Long> {
    List<MatchMember> findByMember(Member loggedInMember);

    List<MatchMember> findByMemberId(Long memberId);

    MatchMember findByMemberAndMatch(Member member, Match match);

    List<MatchMember> findAllByMatch(Match match);
}
