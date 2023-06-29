package com.matchUpSports.boundedContext.field.service;

import com.matchUpSports.boundedContext.field.repository.FieldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FieldService {
    private final FieldRepository fieldRepository;

}
