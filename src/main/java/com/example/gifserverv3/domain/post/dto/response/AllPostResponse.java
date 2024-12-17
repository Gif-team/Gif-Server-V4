package com.example.gifserverv3.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AllPostResponse {

    private List<SinglePostResponse> posts;
}
