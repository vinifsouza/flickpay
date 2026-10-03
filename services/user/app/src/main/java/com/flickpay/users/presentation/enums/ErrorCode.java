package com.flickpay.users.presentation.enums;

public enum ErrorCode {
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS"),
    USER_ALREADY_INACTIVE("USER_ALREADY_INACTIVE");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
