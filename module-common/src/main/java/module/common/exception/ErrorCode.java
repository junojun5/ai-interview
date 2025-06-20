package module.common.exception;

import static module.common.exception.ErrorStatusCode.BAD_GATEWAY;
import static module.common.exception.ErrorStatusCode.BAD_REQUEST;
import static module.common.exception.ErrorStatusCode.CONFLICT;
import static module.common.exception.ErrorStatusCode.FORBIDDEN;
import static module.common.exception.ErrorStatusCode.INTERNAL_SERVER;
import static module.common.exception.ErrorStatusCode.METHOD_NOT_ALLOWED;
import static module.common.exception.ErrorStatusCode.NOT_ACCEPTABLE;
import static module.common.exception.ErrorStatusCode.NOT_FOUND;
import static module.common.exception.ErrorStatusCode.SERVICE_UNAVAILABLE;
import static module.common.exception.ErrorStatusCode.UNAUTHORIZED;
import static module.common.exception.ErrorStatusCode.UNSUPPORTED_MEDIA_TYPE;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
    /**
     * 400 Bad Request
     */
    VALIDATION_EXCEPTION(BAD_REQUEST, "잘못된 요청입니다."),
    VALIDATION_ENUM_VALUE_EXCEPTION(BAD_REQUEST, "잘못된 Enum 값 입니다."),
    VALIDATION_REQUEST_MISSING_EXCEPTION(BAD_REQUEST, "필수적인 요청 값이 입력되지 않았습니다."),
    VALIDATION_WRONG_TYPE_EXCEPTION(BAD_REQUEST, "잘못된 타입이 입력되었습니다."),

    /**
     * 401 UnAuthorized
     */
    UNAUTHORIZED_EXCEPTION(UNAUTHORIZED, "사용자 인증에 실패했습니다."),
    UNAUTHORIZED_INVALID_TOKEN_EXCEPTION(UNAUTHORIZED, "서명이 유효하지 않은 JWT 토큰입니다."),
    UNAUTHORIZED_UNSUPPORTED_TOKEN_EXCEPTION(UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다."),
    UNAUTHORIZED_EMPTY_TOKEN_EXCEPTION(UNAUTHORIZED, "JWT 토큰이 비어있습니다."),
    UNAUTHORIZED_EXPIRED_REFRESH_TOKEN_EXCEPTION(UNAUTHORIZED, "리프레시 토큰 유효시간이 만료되었습니다.\n다시 로그인 해주세요."),
    UNAUTHORIZED_USER_NOT_FOUND_EXCEPTION(UNAUTHORIZED, "존재하지 않는 사용자입니다."),
    UNAUTHORIZED_MISSING_ACCESS_TOKEN(UNAUTHORIZED, "인증에 필요한 JWT 토큰이 존재하지 않습니다."),
    UNAUTHORIZED_UNKNOWN_TOKEN_EXCEPTION(UNAUTHORIZED, "JWT 처리 중 예상치 못한 오류가 발생했습니다."),
    UNAUTHORIZED_INVALID_TOKEN_SUBJECT(UNAUTHORIZED, "JWT의 subject가 유효하지 않습니다."),

    /**
     * 403 Forbidden
     */
    FORBIDDEN_EXCEPTION(FORBIDDEN, "허용하지 않는 요청입니다."),
    FORBIDDEN_FILE_TYPE_EXCEPTION(FORBIDDEN, "허용되지 않은 파일 형식입니다."),
    FORBIDDEN_FILE_NAME_EXCEPTION(FORBIDDEN, "허용되지 않은 파일 이름입니다."),

    /**
     * 404 Not Found
     */
    NOT_FOUND_EXCEPTION(NOT_FOUND, "존재하지 않습니다."),
    NOT_FOUND_USER_EXCEPTION(NOT_FOUND, "존재하지 않는 유저입니다."),

    /**
     * 405 Method Not Allowed
     */
    METHOD_NOT_ALLOWED_EXCEPTION(METHOD_NOT_ALLOWED, "지원하지 않는 메소드 입니다."),

    /**
     * 406 Not Acceptable
     */
    NOT_ACCEPTABLE_EXCEPTION(NOT_ACCEPTABLE, "Not Acceptable"),

    /**
     * 409 Conflict
     */
    CONFLICT_EXCEPTION(CONFLICT, "이미 존재합니다."),
    CONFLICT_REQUEST_EXCEPTION(CONFLICT, "처리중인 요청입니다."),
    CONFLICT_USER_EXCEPTION(CONFLICT, "이미 해당 계정으로 회원가입하셨습니다.\n로그인 해주세요."),
    CONFLICT_LOGIN_EXCEPTION(CONFLICT, "이미 로그인 중인 유저입니다."),

    /**
     * 415 Unsupported Media Type
     */
    UNSUPPORTED_MEDIA_TYPE_EXCEPTION(UNSUPPORTED_MEDIA_TYPE, "해당하는 미디어 타입을 지원하지 않습니다."),

    /**
     * 500 Internal Server Exception
     */
    INTERNAL_SERVER_EXCEPTION(INTERNAL_SERVER, "예상치 못한 서버 에러가 발생하였습니다."),

    /**
     * 502 Bad Gateway
     */
    BAD_GATEWAY_EXCEPTION(BAD_GATEWAY, "일시적인 에러가 발생하였습니다.\n잠시 후 다시 시도해주세요!"),

    /**
     * 503 Service UnAvailable
     */
    SERVICE_UNAVAILABLE_EXCEPTION(SERVICE_UNAVAILABLE, "현재 점검 중입니다.\n잠시 후 다시 시도해주세요!");

    private final ErrorStatusCode statusCode;
    private final String message;

    public int getStatus() {
        return statusCode.getStatus();
    }
}
