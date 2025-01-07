package module.common.exception;

import lombok.Getter;

@Getter
public class ConflictException extends CustomException {

    public ConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ConflictException(String message) {
        super(message, ErrorCode.CONFLICT_EXCEPTION);
    }
}
