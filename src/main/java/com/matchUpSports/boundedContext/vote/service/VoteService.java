package com.matchUpSports.boundedContext.vote.service;

import com.matchUpSports.boundedContext.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {
    private final VoteRepository voteRepository;

}
