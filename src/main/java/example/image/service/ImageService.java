package example.image.service;

import example.domain.images.Image;
import example.domain.images.ImageRepository;
import example.global.exception.CustomApplicationException;
import example.global.exception.ErrorCode;
import example.image.service.dto.ImageUploadInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Client s3Client;
    private final ImageRepository imageRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * [public 메서드]
     * - 외부에서 사용, DB에 저장된 이미지의 uploadToken을 반환
     */
    public String upload(ImageUploadInfo imageUploadInfo) {
        // [Step 1] 요청값에 uploadToken 이 존재하지 않을 경우 uploadToken 생성
        String uploadToken = (imageUploadInfo.getUploadToken() == null || imageUploadInfo.getUploadToken().isBlank())
                ? UUID.randomUUID().toString()
                : imageUploadInfo.getUploadToken();

        // [Step 2] 모든 파일 유효성 검사
        imageUploadInfo.getImages().forEach(file -> validateFile(file.getOriginalFilename()));

        // [Step 3] 모든 파일 검증 완료 후 업로드
        List<String> imageUrls = imageUploadInfo.getImages().stream()
                .map(this::uploadImageToS3)
                .toList();

        // [Step 4] 업로드 된 파일 DB 저장
        createImage(imageUrls, uploadToken);

        // [Step 5] uploadToken 반환
        return uploadToken;
    }

    /**
     * [private 메서드]
     * - 파일 유효성 검증
     */
    private void validateFile(String filename) {
        // [Step 2-1] 파일 존재 유무 검증
        if (filename == null || filename.isEmpty()) {
            throw new CustomApplicationException(ErrorCode.NOT_EXIST_FILE);
        }

        // [Step 2-2] 확장자 존재 유무 검증
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new CustomApplicationException(ErrorCode.NOT_EXIST_FILE_EXTENSION);
        }

        // [Step 2-3] 허용되지 않는 확장자 검증
        String extension = URLConnection.guessContentTypeFromName(filename);
        List<String> allowedExtentionList = Arrays.asList("image/jpg", "image/jpeg", "image/png", "image/gif");
        if (extension == null || !allowedExtentionList.contains(extension)) {
            throw new CustomApplicationException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    /**
     * [private 메서드]
     * - S3 업로드
     */
    private String uploadImageToS3(MultipartFile image) {
        String originalFilename = image.getOriginalFilename(); // 원본 파일 명
        String extension = Objects.requireNonNull(originalFilename)
                .substring(originalFilename.lastIndexOf(".") + 1); // 확장자 명
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "_" + originalFilename; // 변경된 파일

        // [Step 3-1] 이미지 파일 -> InputStream 변환
        try (InputStream inputStream = image.getInputStream()) {
            // PutObjectRequest 객체 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName) // 버킷 이름
                    .key(s3FileName) // 저장할 파일 이름
                    .acl(ObjectCannedACL.PUBLIC_READ) // 퍼블릭 읽기 권한
                    .contentType("image/" + extension) // 이미지 MIME 타입
                    .contentLength(image.getSize()) // 파일 크기
                    .build();

            // [Step 3-2] S3에 이미지 업로드
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, image.getSize()));

        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new CustomApplicationException(ErrorCode.IO_EXCEPTION_UPLOAD_FILE);
        }

        // [Step 3-3] public url 반환
        return s3Client.utilities().getUrl(url -> url.bucket(bucketName).key(s3FileName)).toString();
    }

    /**
     * [private 메서드]
     * DB에 업로드된 이미지 저장
     */
    private void createImage(List<String> imageUrls, String uploadToken) {
        // [Step 4-1] DB 저장을 위한 entity 객체생성
        List<Image> images = imageUrls.stream()
                .map(url -> Image.create(url, uploadToken, null))
                .toList();

        // [Step 4-2] 이미지 저장
        imageRepository.saveAll(images);
    }

    // [public 메서드] 이미지의 public url을 이용하여 S3에서 해당 이미지를 제거, getKeyFromImageAddress 메서드를 호출하여 삭제에 필요한 key 획득
    public void delete(List<String> imageUrls) {
        List<String> keys = imageUrls.stream()
                .map(this::getKeyFromImageUrls)
                .toList();

        try {
            // S3에서 파일을 삭제하기 위한 요청 객체 생성
            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName) // S3 버킷 이름 지정
                    .delete(delete -> delete.objects(
                            // S3 객체들을 삭제할 객체 목록을 생성
                            keys.stream()
                                    .map(key -> ObjectIdentifier.builder().key(key).build())
                                    .toList()
                    ))
                    .build();
            s3Client.deleteObjects(deleteObjectsRequest); // S3에서 객체 삭제
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new CustomApplicationException(ErrorCode.IO_EXCEPTION_DELETE_FILE);
        }
    }

    // [private 메서드] 삭제에 필요한 key 반환
    private String getKeyFromImageUrls(String imageUrl) {
        try {
            URL url = new URI(imageUrl).toURL(); // 인코딩된 주소를 URI 객체로 변환 후 URL 객체로 변환
            String decodedKey = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);// URI에서 경로 부분을 가져와 URL 디코딩을 통해 실제 키로 변환
            return decodedKey.substring(1); // 경로 앞에 '/'가 있으므로 이를 제거한 뒤 반환
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new CustomApplicationException(ErrorCode.INVALID_URL_FORMAT);
        }
    }

}
