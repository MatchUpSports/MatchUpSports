package com.matchUpSports.boundedContext.match.repository;

import com.matchUpSports.boundedContext.match.entity.MatchMember;
import com.matchUpSports.boundedContext.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchMemberRepository extends JpaRepository<MatchMember, Long> {
    List<MatchMember> findByMember(Member loggedInMember);
}
