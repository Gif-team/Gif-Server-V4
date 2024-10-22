package com.example.gifserverv3.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 에러 응답 형식 지정
@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final int statusCode;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.statusCode = errorCode.getHttpStatus().value();
        this.message = errorCode.getMessage();
    }
}