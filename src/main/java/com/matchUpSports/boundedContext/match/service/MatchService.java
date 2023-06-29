package com.matchUpSports.boundedContext.match.service;

import com.matchUpSports.boundedContext.match.repository.MatchMemberRepository;
import com.matchUpSports.boundedContext.match.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {
    private final MatchRepository matchRepository;
    private final MatchMemberRepository matchMemberRepository;

}
