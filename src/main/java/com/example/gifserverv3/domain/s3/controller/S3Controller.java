package com.example.gifserverv3.domain.s3.controller;


import com.example.gifserverv3.domain.s3.dto.request.PresignedUrlRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Client s3Client;

    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    public S3Controller(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @PostMapping("/presigned-url")
    public String getPresignedUrl(@RequestBody PresignedUrlRequest request) {
        S3Presigner presigner = S3Presigner.create();
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key("images/"+request.getFileName())
                .contentType(request.getFileType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        URL presignedUrl = presigner.presignPutObject(presignRequest).url();
        return presignedUrl.toString();
    }
}
