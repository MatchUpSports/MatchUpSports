package com.matchUpSports.boundedContext.vote.repository;

import com.matchUpSports.boundedContext.vote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
