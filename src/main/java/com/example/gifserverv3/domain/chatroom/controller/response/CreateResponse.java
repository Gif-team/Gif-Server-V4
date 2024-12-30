package com.example.gifserverv3.domain.chatroom.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreateResponse {
    private Long roomId;
    private LocalDateTime createdAt;
}

