package com.example.gifserverv3.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SinglePostResponse {

    private Long id;
    private String title;
    private String content;
    private boolean category;
    private double price;
    private LocalDateTime realtime;
    private String writer;
    private Long writerId;
    private int numberLike;
    private BuildingResponseDto building;

    // 빌딩 정보 DTO
    @Data
    @AllArgsConstructor
    public static class BuildingResponseDto {
        private int id;
        private int floor;
    }
}
