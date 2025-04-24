package example.image.controller.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageUploadResponse {

    private String uploadToken;

    public ImageUploadResponse(String uploadToken) {
        this.uploadToken = uploadToken;
    }

    public static ImageUploadResponse of(String uploadToken) {
        return new ImageUploadResponse(uploadToken);
    }
}
