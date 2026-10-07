package com.flickpay.users.presentation.exception;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.flickpay.users.domain.exception.EmailAlreadyExistsException;
import com.flickpay.users.domain.exception.UserAlreadyInactiveException;
import com.flickpay.users.domain.exception.UserNotFoundException;
import com.flickpay.shared.presentation.dto.ApiResponse;
import com.flickpay.shared.presentation.dto.ErrorResponse;
import com.flickpay.users.presentation.enums.ErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler {
    final String BASE_PATH = "/users";

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        ErrorResponse response = new ErrorResponse(
            ErrorCode.EMAIL_ALREADY_EXISTS.getCode(),
            ex.getMessage(),
            OffsetDateTime.now(),
            BASE_PATH
        );

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ApiResponse<>(response));
    }

    @ExceptionHandler (UserAlreadyInactiveException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUserAlreadyInactiveException(UserAlreadyInactiveException ex) {
        ErrorResponse response = new ErrorResponse(
            ErrorCode.USER_ALREADY_INACTIVE.getCode(),
            ex.getMessage(),
            OffsetDateTime.now(),
            BASE_PATH
        );
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ApiResponse<>(response));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
            ErrorCode.USER_NOT_FOUND.getCode(),
            ex.getMessage(),
            OffsetDateTime.now(),
            BASE_PATH
        );
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse<>(response));
    }
}
