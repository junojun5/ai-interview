package module.common.exception;

import lombok.Getter;

@Getter
public class InternalServerException extends CustomException {

    public InternalServerException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public InternalServerException(String message) {
        super(message, ErrorCode.INTERNAL_SERVER_EXCEPTION);
    }
}
