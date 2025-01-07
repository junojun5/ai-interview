package module.common.exception;

import lombok.Getter;

@Getter
public abstract class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    protected CustomException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getStatus() {
        return errorCode.getStatus();
    }
}
