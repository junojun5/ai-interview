package module.common.exception;

import lombok.Getter;

@Getter
public class ForbiddenException extends CustomException {

    public ForbiddenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ForbiddenException(String message) {
        super(message, ErrorCode.FORBIDDEN_EXCEPTION);
    }
}
