package com.flickpay.users.presentation.dto;

import java.time.OffsetDateTime;

public record ErrorResponse(
    String errorCode,
    String errorMessage,
    OffsetDateTime timestamp,
    String path
) {}
