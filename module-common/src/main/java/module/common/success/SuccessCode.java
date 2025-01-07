package module.common.success;

import static module.common.success.SuccessStatusCode.OK;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
    /**
     * 200 OK
     */
    OK_SUCCESS(OK, "성공입니다."),

    /**
     * 201 CREATED
     */
    CREATED_SUCCESS(SuccessStatusCode.CREATED, "생성에 성공하였습니다.");

    private final SuccessStatusCode statusCode;
    private final String message;

    public int getStatus() {
        return statusCode.getStatus();
    }
}
