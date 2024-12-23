package com.example.gifserverv3.domain.post.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "posts")
@Builder
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postId")
    private Long postid;

    @Column(nullable = false)
    @NotNull
    private String title;

    @Column(length = 5000, nullable = false)
    @NotNull
    private String content;

    @Column(nullable = false)
    @NotNull
    private boolean category;

    @Column(nullable = false)
    @NotNull
    private double price;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime realtime;

    @Embedded
    @NotNull
    private Building building;

    @Column(nullable = false)
    @NotNull
    private String writer;

    @Column(nullable = false, name = "userId")
    @NotNull
    private Long writerId;

    @Column(nullable = false, name = "likeNumber")
    @NotNull
    @Builder.Default
    private int likeNumber = 0;

    @Embeddable
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    @NotNull
    public static class Building {

        @Min(value = 1, message = "Building ID must be between 1 and 3.")
        @Max(value = 3, message = "Building ID must be between 1 and 3.")
        @Column(name = "building_id", nullable = false)
        @NotNull
        private int id;

        @Min(value = 1, message = "Floor must be between 1 and 5.")
        @Max(value = 5, message = "Floor must be between 1 and 5.")
        @Column(nullable = false)
        @NotNull
        private int floor;

        public Building(int id, int floor) {
            this.id = id;
            this.floor = floor;
        }
    }
}



