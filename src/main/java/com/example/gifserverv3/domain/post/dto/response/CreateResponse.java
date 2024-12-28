package com.example.gifserverv3.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateResponse {
    private String message;
    private int statusCode;
    private Long postId; // 추가된 필드
}
