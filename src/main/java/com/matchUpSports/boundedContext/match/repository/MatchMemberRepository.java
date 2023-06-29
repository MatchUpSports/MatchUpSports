package com.matchUpSports.boundedContext.match.repository;

import com.matchUpSports.boundedContext.match.entity.MatchMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchMemberRepository extends JpaRepository<MatchMember, Long> {

}
