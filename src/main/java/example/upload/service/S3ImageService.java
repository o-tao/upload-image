package example.upload.service;

import example.global.exception.CustomApplicationException;
import example.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ImageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public List<String> upload(List<MultipartFile> files) {
        // 빈 파일 검증
        if (files.isEmpty()) {
            throw new CustomApplicationException(ErrorCode.EMPTY_FILE);
        }
        // 각 파일을 업로드하고 url을 리스트로 반환
        return files.stream()
                .map(this::uploadImage)
                .toList();
    }

    private String uploadImage(MultipartFile file) {
        validateFile(file.getOriginalFilename()); // 파일 유효성 검증
        try {
            return uploadImageToS3(file); // 이미지를 S3에 업로드하고, 저장된 파일의 public url을 서비스 로직에 반환
        } catch (IOException e) {
            throw new CustomApplicationException(ErrorCode.IO_EXCEPTION_UPLOAD_FILE);
        }
    }

    private void validateFile(String filename) {
        // 파일 이름 존재 유무 검증
        if (filename == null || filename.isEmpty()) {
            throw new CustomApplicationException(ErrorCode.NOT_EXIST_FILE_NAME);
        }

        // 확장자 존재 유무 검증
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new CustomApplicationException(ErrorCode.NOT_EXIST_FILE_EXTENSION);
        }

        // 허용되지 않는 확장자 검증
        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");
        if (!allowedExtentionList.contains(extension)) {
            throw new CustomApplicationException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    private String uploadImageToS3(MultipartFile file) throws IOException {
        // 원본 파일 명
        String originalFilename = file.getOriginalFilename();
        // 확장자 명
        String extension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".") + 1);
        // 변경된 파일
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "_" + originalFilename;

        // 이미지 파일 -> InputStream 변환
        try (InputStream inputStream = file.getInputStream()) {
            // PutObjectRequest 객체 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName) // 버킷 이름
                    .key(s3FileName) // 저장할 파일 이름
                    .acl(ObjectCannedACL.PUBLIC_READ) // 퍼블릭 읽기 권한
                    .contentType("image/" + extension) // 이미지 MIME 타입
                    .contentLength(file.getSize()) // 파일 크기
                    .build();
            // S3에 이미지 업로드
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (Exception exception) {
            throw new CustomApplicationException(ErrorCode.IO_EXCEPTION_UPLOAD_FILE);
        }

        // public url 반환
        return s3Client.utilities().getUrl(url -> url.bucket(bucketName).key(s3FileName)).toString();
    }

}
