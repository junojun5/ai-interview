package module.common.exception;

import lombok.Getter;

@Getter
public class ValidationException extends CustomException {

    public ValidationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ValidationException(String message) {
        super(message, ErrorCode.VALIDATION_EXCEPTION);
    }
}
