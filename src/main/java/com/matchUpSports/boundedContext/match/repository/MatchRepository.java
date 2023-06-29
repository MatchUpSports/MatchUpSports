package com.matchUpSports.boundedContext.match.repository;

import com.matchUpSports.boundedContext.match.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
