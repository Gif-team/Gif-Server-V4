package com.example.gifserverv3.global.type;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private ErrorCode errorCode;
    private String errorMessage;
    private Map<String, String> validationField;

    public ErrorResponse(ErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.validationField = null;
    }

}
