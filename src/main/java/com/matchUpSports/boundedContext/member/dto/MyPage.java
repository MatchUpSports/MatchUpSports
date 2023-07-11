package com.matchUpSports.boundedContext.member.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@SuperBuilder
@Getter
public class MyPage extends ModifyingDisplaying {
    private LocalDate createdDate;
    private String username;
    private String nickname;
    private String winningRate;
    private List<String> authorities;
}
