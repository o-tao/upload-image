package example.image.service.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageUploadInfo {

    private List<MultipartFile> images;
    private String uploadToken;

    public ImageUploadInfo(List<MultipartFile> images, String uploadToken) {
        this.images = images;
        this.uploadToken = uploadToken;
    }
}
