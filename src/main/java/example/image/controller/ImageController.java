package example.image.controller;

import example.image.controller.dto.ImageDeleteRequest;
import example.image.controller.dto.ImageUploadRequest;
import example.image.controller.dto.ImageUploadResponse;
import example.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ImageUploadResponse s3Upload(@ModelAttribute ImageUploadRequest imageUploadRequest) {
        String upload = imageService.upload(imageUploadRequest.toUpload());
        return ImageUploadResponse.of(upload);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> s3Delete(@RequestBody ImageDeleteRequest imageDeleteRequest) {
        imageService.delete(imageDeleteRequest.getImageUrls());
        return ResponseEntity.ok("이미지 삭제 성공");
    }

}
