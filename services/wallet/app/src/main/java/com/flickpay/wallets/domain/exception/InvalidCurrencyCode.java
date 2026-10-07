package com.flickpay.wallets.domain.exception;

public class InvalidCurrencyCode extends RuntimeException {
    public InvalidCurrencyCode(String currencyCode) {
        super("Invalid currency code: " + currencyCode);
    }
}
