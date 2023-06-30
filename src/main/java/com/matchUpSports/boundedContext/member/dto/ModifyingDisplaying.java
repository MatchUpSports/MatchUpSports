package com.matchUpSports.boundedContext.member.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ModifyingDisplaying {
    @Email
    private String email;
    private String phone;
    private String area;
    private String tier;
}
