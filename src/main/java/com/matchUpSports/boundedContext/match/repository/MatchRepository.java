package com.matchUpSports.boundedContext.match.repository;

import com.matchUpSports.boundedContext.match.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByStadiumAndMatchDateAndUsageTime(String stadium, LocalDate matchDate, Integer usageTime);

}
