package com.matchUpSports.base.rsData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RsData<T> {
    private Status statusCode;
    private String msg;
    private T content;

    public static <T> RsData<T> of(Status statusCode, String msg, T data) {
        return new RsData<>(statusCode, msg, data);
    }

    public boolean isSuccess() {
        return statusCode.equals(Status.SUCCESS);
    }

    public enum Status {
        SUCCESS, FAIL
    }
}