package module.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorStatusCode {
    // 클라이언트 요청이 잘못되었거나 유효하지 않은 경우
    BAD_REQUEST(400),
    // 클라이언트가 인증에 실패한 경우
    UNAUTHORIZED(401),
    // 권한이 없어 요청이 금지된 경우
    FORBIDDEN(403),
    // 요청한 리소스를 찾을 수 없는 경우
    NOT_FOUND(404),
    // 허용되지 않은 HTTP 메서드로 요청한 경우
    METHOD_NOT_ALLOWED(405),
    // 클라이언트가 요청한 응답 형식을 제공할 수 없는 경우
    // ex) Response가 반환가능한 형식이 "application/json"뿐인데, 클라이언트가 accept로 "application/xml"을 요청한 경우
    NOT_ACCEPTABLE(406),
    // 리소스의 현재 상태와 충돌이 발생한 경우 ex) 중복 데이터 생성, 상태 불일치 등
    CONFLICT(409),
    // 클라이언트가 서버가 처리할 수 없는 형식으로 요청할 경우
    UNSUPPORTED_MEDIA_TYPE(415),
    // 서버에서 처리 중에 예기치 않은 오류가 발생한 경우
    INTERNAL_SERVER(500),
    // 게이트웨이 또는 프록시 서버에서 잘못된 응답을 받은 경우
    BAD_GATEWAY(502),
    // 서버가 과부하 또는 유지보수로 인해 서비스를 사용할 수 없는 경우
    SERVICE_UNAVAILABLE(503);

    private final int status;
}
