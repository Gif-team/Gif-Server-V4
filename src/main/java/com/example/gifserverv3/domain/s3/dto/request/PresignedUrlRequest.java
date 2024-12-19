package com.example.gifserverv3.domain.s3.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlRequest {
    private String fileName;
    private String fileType;
}
