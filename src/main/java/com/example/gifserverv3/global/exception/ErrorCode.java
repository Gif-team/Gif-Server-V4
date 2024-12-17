package com.example.gifserverv3.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 예외 케이스 관리
@Getter
@RequiredArgsConstructor
public enum ErrorCode {


    DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "중복된 사용자 입니다"),

    MANY_BUILDING(HttpStatus.BAD_REQUEST, "건물은 1에서 3까지 등록 가능합니다."),

    MANY_FLOOR(HttpStatus.BAD_REQUEST, "층은 1에서 5까지 등록 가능합니다."),

    NOT_GSM_EMAIL(HttpStatus.BAD_REQUEST, "학교 계정으로만 회원가입할 수 있습니다."),

    NOT_MATCH_INFORMATION(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."),

    NOT_MATCH_POST(HttpStatus.BAD_REQUEST, "게시물을 찾을 수 없습니다."),

    INVALID_SESSION(HttpStatus.BAD_REQUEST, "세션이 유효하지 않습니다."),

    INVALID_AUTHORIZED(HttpStatus.BAD_REQUEST, "권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
