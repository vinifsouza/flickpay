package com.flickpay.wallets.presentation.exception;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.flickpay.shared.presentation.dto.ApiResponse;
import com.flickpay.shared.presentation.dto.ErrorResponse;
import com.flickpay.wallets.domain.exception.IllegalWalletStatusChangeException;
import com.flickpay.wallets.domain.exception.InvalidCurrencyCode;
import com.flickpay.wallets.domain.exception.WalletNotFoundException;
import com.flickpay.wallets.presentation.enums.ErrorCode;

public class GlobalExceptionHandler {
    final String BASE_PATH = "/wallets";

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleWalletNotFoundException(WalletNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.WALLET_NOT_FOUND.getCode(),
                ex.getMessage(),
                OffsetDateTime.now(),
                BASE_PATH
        );

        ApiResponse<ErrorResponse> apiResponse = new ApiResponse<>(errorResponse);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(IllegalWalletStatusChangeException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalWalletStatusChangeException(IllegalWalletStatusChangeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.ILLEGAL_WALLET_STATUS_CHANGE.getCode(),
                ex.getMessage(),
                OffsetDateTime.now(),
                BASE_PATH
        );

        ApiResponse<ErrorResponse> apiResponse = new ApiResponse<>(errorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(InvalidCurrencyCode.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleInvalidCurrencyCode(InvalidCurrencyCode ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_CURRENCY_CODE.getCode(),
                ex.getMessage(),
                OffsetDateTime.now(),
                BASE_PATH
        );

        ApiResponse<ErrorResponse> apiResponse = new ApiResponse<>(errorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
