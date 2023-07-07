package com.matchUpSports.boundedContext.match.repository;

import com.matchUpSports.boundedContext.match.entity.MatchVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchVoteRepository extends JpaRepository<MatchVote, Long> {
}
