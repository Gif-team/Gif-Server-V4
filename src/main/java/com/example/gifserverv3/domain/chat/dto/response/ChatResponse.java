package com.example.gifserverv3.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private Long id;
    private Sender sender;
    private String message;
    private LocalDateTime sendDate;

}
