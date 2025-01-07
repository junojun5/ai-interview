package server.api.advice;

import static module.common.exception.ErrorCode.INTERNAL_SERVER_EXCEPTION;
import static module.common.exception.ErrorCode.METHOD_NOT_ALLOWED_EXCEPTION;
import static module.common.exception.ErrorCode.NOT_ACCEPTABLE_EXCEPTION;
import static module.common.exception.ErrorCode.UNSUPPORTED_MEDIA_TYPE_EXCEPTION;
import static module.common.exception.ErrorCode.VALIDATION_ENUM_VALUE_EXCEPTION;
import static module.common.exception.ErrorCode.VALIDATION_EXCEPTION;
import static module.common.exception.ErrorCode.VALIDATION_REQUEST_MISSING_EXCEPTION;
import static module.common.exception.ErrorCode.VALIDATION_WRONG_TYPE_EXCEPTION;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import module.common.dto.ErrorResponse;
import module.common.exception.CustomException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvice {

    /*
     * Custom Exception
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleBaseException(CustomException exception) {
        if (exception.getStatus() >= 400 && exception.getStatus() < 500) {
            log.warn(exception.getMessage(), exception);
        } else {
            log.error(exception.getMessage(), exception);
        }
        return ResponseEntity.status(exception.getStatus())
            .body(ErrorResponse.error(exception.getErrorCode()));
    }

    /*
     * 400 BadRequest
     * Spring Validation exception
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    protected ErrorResponse handleBadRequest(final BindException exception) {
        log.warn(exception.getMessage());
        FieldError fieldError = Objects.requireNonNull(exception.getFieldError());
        return ErrorResponse.error(VALIDATION_EXCEPTION,
            String.format("%s (%s)", fieldError.getDefaultMessage(), fieldError.getField()));
    }

    /*
     * 400 BadRequest
     * 잘못된 Enum 값이 입력된 경우 발생하는 Exception
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ErrorResponse handleHttpMessageNotReadableException(
        final HttpMessageNotReadableException exception) {
        log.warn(exception.getMessage());
        return ErrorResponse.error(VALIDATION_ENUM_VALUE_EXCEPTION);
    }

    /*
     * 400 BadRequest
     * RequestParam, RequestPath, RequestPart 등의 필드가 입력되지 않은 경우 발생하는 Exception
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestValueException.class)
    protected ErrorResponse handle(final MissingRequestValueException exception) {
        log.warn(exception.getMessage());
        return ErrorResponse.error(VALIDATION_REQUEST_MISSING_EXCEPTION);
    }

    /*
     * 400 BadRequest
     * 잘못된 타입이 입력된 경우 발생하는 Exception
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TypeMismatchException.class)
    protected ErrorResponse handleTypeMismatchException(final TypeMismatchException exception) {
        log.warn(exception.getMessage());
        return ErrorResponse.error(VALIDATION_WRONG_TYPE_EXCEPTION,
            String.format("%s (%s)", VALIDATION_WRONG_TYPE_EXCEPTION.getMessage(),
                exception.getValue()));
    }

    /*
     * 405 Method Not Allowed
     * 지원하지 않은 HTTP method 호출 할 경우 발생하는 Exception
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ErrorResponse handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException exception) {
        log.warn(exception.getMessage());
        return ErrorResponse.error(METHOD_NOT_ALLOWED_EXCEPTION);
    }

    /*
     * 406 Not Acceptable
     * 사용자가 요청한 응답형식을 지원하지 않는 경우 발생하는 Exception
     */
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    protected ErrorResponse handleHttpMediaTypeNotAcceptableException(
        HttpMediaTypeNotAcceptableException exception) {
        log.warn(exception.getMessage());
        return ErrorResponse.error(NOT_ACCEPTABLE_EXCEPTION);
    }

    /*
     * 415 UnSupported Media Type
     * 지원하지 않는 미디어 타입인 경우 발생하는 Exception
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeException.class)
    protected ErrorResponse handleHttpMediaTypeException(final HttpMediaTypeException exception) {
        log.warn(exception.getMessage(), exception);
        return ErrorResponse.error(UNSUPPORTED_MEDIA_TYPE_EXCEPTION);
    }

    /*
     * 500 Internal Server
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected ErrorResponse handleException(final Exception exception) {
        log.error(exception.getMessage(), exception);
        return ErrorResponse.error(INTERNAL_SERVER_EXCEPTION);
    }
}
