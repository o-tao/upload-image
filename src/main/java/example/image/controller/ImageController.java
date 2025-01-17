package example.image.controller;

import example.image.controller.dto.ImageDeleteRequest;
import example.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> s3Upload(@RequestPart(value = "image", required = false) List<MultipartFile> multipartFile) {
        List<String> upload = imageService.upload(multipartFile);
        return ResponseEntity.ok(upload);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> s3Delete(@RequestBody ImageDeleteRequest imageDeleteRequest) {
        imageService.delete(imageDeleteRequest.getImageUrls());
        return ResponseEntity.ok("이미지 삭제 성공");
    }

}
