package com.example.gifserverv3.domain.image.controller;

import com.example.gifserverv3.domain.image.dto.request.SaveImageRequest;
import com.example.gifserverv3.domain.image.entity.ImageEntity;
import com.example.gifserverv3.domain.image.repository.ImageRepository;
import com.example.gifserverv3.global.util.MsgResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/image")
public class ImageController {

    private final ImageRepository imageRepository;

    private final S3Client s3Client;

    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    public ImageController(ImageRepository imageRepository, S3Client s3Client) {
        this.imageRepository = imageRepository;
        this.s3Client = s3Client;
    }

    @PostMapping("/save-image")
    public ResponseEntity<MsgResponseDto> saveImage(@RequestBody SaveImageRequest request) {
        ImageEntity image = new ImageEntity();
        image.setPostId(request.getPostId());
        image.setFileName(request.getFileName());
        imageRepository.save(image);

        MsgResponseDto responseDto = new MsgResponseDto("이미지 속성이 저장되었습니다.", 200);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{postId}")
    public List<String> getImagesForPost(@PathVariable Long postId) {
        List<ImageEntity> imageEntities = imageRepository.findByPostId(postId);

        return imageEntities.stream().map(imageEntity -> {
            S3Presigner presigner = S3Presigner.create();
            return presigner.presignGetObject(req -> req
                            .getObjectRequest(b -> b.bucket(bucketName).key("images/" + imageEntity.getFileName()))
                            .signatureDuration(Duration.ofMinutes(10)))
                    .url().toString();
        }).collect(Collectors.toList());
    }
}
