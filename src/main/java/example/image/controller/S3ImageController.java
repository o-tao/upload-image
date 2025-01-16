package example.image.controller;

import example.image.controller.dto.ImageDeleteRequest;
import example.image.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3ImageController {

    private final S3ImageService s3ImageService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> s3Upload(@RequestPart(value = "image", required = false) List<MultipartFile> multipartFile) {
        List<String> upload = s3ImageService.upload(multipartFile);
        return ResponseEntity.ok(upload);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> s3Delete(@RequestBody ImageDeleteRequest imageDeleteRequest) {
        s3ImageService.delete(imageDeleteRequest.getImageUrls());
        return ResponseEntity.ok("이미지 삭제 성공");
    }

}
