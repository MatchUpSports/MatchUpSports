package com.matchUpSports.boundedContext.member.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicUserInfoForm {
    @Email
    private String email;
    private String phone;
    private String area;
    private String tier;
}
