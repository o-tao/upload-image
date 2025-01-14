package example.global.exception.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class CustomExceptionResponse {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final int code;
    private final String message;

    public CustomExceptionResponse(HttpStatusCode status, String message) {
        this.code = status.value();
        this.message = message;
    }
}
