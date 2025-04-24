package example.image.controller.dto;

import example.image.service.dto.ImageUploadInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageUploadRequest {

    private List<MultipartFile> images;
    private String uploadToken;

    public ImageUploadRequest(List<MultipartFile> images, String uploadToken) {
        this.images = images;
        this.uploadToken = uploadToken;
    }

    public ImageUploadInfo toUpload() {
        return new ImageUploadInfo(images, uploadToken);
    }
}
