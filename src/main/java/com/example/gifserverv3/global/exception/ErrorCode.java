package com.example.gifserverv3.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 예외 케이스 관리
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_MATCH_ADMIN_TOKEN(HttpStatus.BAD_REQUEST, "관리자 암호가 일치하지 않습니다."),

    DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "중복된 사용자 입니다"),

    NOT_GSM_EMAIL(HttpStatus.BAD_REQUEST, "학교 계정으로만 회원가입할 수 있습니다."),

    NOT_MATCH_INFORMATION(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."),

    INVALID_SESSION(HttpStatus.BAD_REQUEST, "세션이 유효하지 않습니다."),

    INVALID_IMAGE_EXTENSION(HttpStatus.BAD_REQUEST, "잘못된 이미지 확장자 입니다."),

    NOT_OK_REQUEST(HttpStatus.BAD_REQUEST, "유효한 요청이 아닙니다."),

    DONT_SEND_REQUEST(HttpStatus.BAD_REQUEST, "요청을 보낼 수 없습니다."),

    MANY_REQUEST(HttpStatus.BAD_REQUEST, "요청은 한번에 3개를 초과할 수 없습니다."),

    MANY_IMAGES(HttpStatus.BAD_REQUEST, "이미지는 최대 3장까지 업로드 가능합니다."),

    NOT_FOUND_REQUEST(HttpStatus.BAD_REQUEST, "요청을 찾을 수 없습니다."),

    NOT_FOUND_CHAT(HttpStatus.BAD_REQUEST, "채팅을 찾을 수 없습니다."),

    DONT_ACCESS_CHAT(HttpStatus.BAD_REQUEST, "해당 채팅에 참여자가 아닙니다."),

    DUPLICATED_CHAT(HttpStatus.BAD_REQUEST, "중복된 채팅방 생성 요청입니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
