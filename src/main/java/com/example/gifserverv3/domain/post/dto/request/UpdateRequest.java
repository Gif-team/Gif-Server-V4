package com.example.gifserverv3.domain.post.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
public class UpdateRequest {

    private String title;

    private String content;

    private boolean category;

    private double price;

    private BuildingRequest building;

    @Data
    public static class BuildingRequest {
        @Min(value = 1, message = "Building ID must be between 1 and 3.")
        @Max(value = 3, message = "Building ID must be between 1 and 3.")
        private int id;

        @Min(value = 1, message = "Floor must be between 1 and 5.")
        @Max(value = 5, message = "Floor must be between 1 and 5.")
        private int floor;
    }
}
