package server.api.advice;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebDataBinderAdvice {

    /*
     * GET 메소드의 DTO에서 setter메서드를 생성하지 않아도 됨
     * 참고) https://jojoldu.tistory.com/407
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
    }
}
