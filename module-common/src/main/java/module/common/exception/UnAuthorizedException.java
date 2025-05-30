package module.common.exception;

import lombok.Getter;

@Getter
public class UnAuthorizedException extends CustomException {

    public UnAuthorizedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UnAuthorizedException(String message) {
        super(message, ErrorCode.UNAUTHORIZED_EXCEPTION);
    }

    public UnAuthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
