package com.flickpay.wallets.presentation.enums;

public enum ErrorCode {
    WALLET_NOT_FOUND("WALLET_NOT_FOUND", "Wallet not found"),
    INVALID_CURRENCY_CODE("INVALID_CURRENCY_CODE", "Invalid currency code"),
    INVALID_WALLET_STATUS("INVALID_WALLET_STATUS", "Invalid wallet status"),
    ILLEGAL_WALLET_STATUS_CHANGE("ILLEGAL_WALLET_STATUS_CHANGE", "Illegal wallet status change");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
