package example.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    IO_EXCEPTION_UPLOAD_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 문제가 발생했습니다. 다시 시도해 주세요."),
    NOT_EXIST_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "파일 확장자가 존재하지 않습니다."),
    NOT_EXIST_FILE(HttpStatus.BAD_REQUEST, "파일이 존재하지 않습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "허용되지 않는 파일 확장자입니다."),
    IO_EXCEPTION_DELETE_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 중 문제가 발생했습니다. 다시 시도해 주세요."),
    INVALID_URL_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 URL 형식입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
