package module.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import module.common.success.SuccessCode;
import org.springframework.http.ResponseEntity;

// TODO ToString 테스트 필요
// @ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SuccessResponse<T> {

    private int status;
    private boolean success;
    private String message;
    private T data;

    public static final ResponseEntity<SuccessResponse<String>> OK = success(SuccessCode.OK_SUCCESS,
        null);
    public static final ResponseEntity<SuccessResponse<String>> CREATED = success(
        SuccessCode.CREATED_SUCCESS, null);

    public static <T> ResponseEntity<SuccessResponse<T>> success(SuccessCode successCode, T data) {
        return ResponseEntity
            .status(successCode.getStatus())
            .body(new SuccessResponse<>(successCode.getStatus(), true, successCode.getMessage(),
                data));
    }
}
