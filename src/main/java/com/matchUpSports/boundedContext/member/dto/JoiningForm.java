package com.matchUpSports.boundedContext.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoiningForm extends BasicUserInfoForm {
    private String authorities;
}
