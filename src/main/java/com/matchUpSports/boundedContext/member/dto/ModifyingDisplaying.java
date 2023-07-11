package com.matchUpSports.boundedContext.member.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ModifyingDisplaying {
    private String nickname;
    @Email
    private String email;
    private String phone;
    private String bigDistrict;
    private String smallDistrict;
    private String tier;
}