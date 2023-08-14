package com.basic.orderapi.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ExceptionResponse> customRequestException(final CustomException c) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(c.getMessage(), c.getErrorCode(), c.getStatus()));
    }

//    @ExceptionHandler({ServletException.class})
//    public ResponseEntity<String> ServletException(final CustomException c) {
//        return ResponseEntity.badRequest().body("잘못된 인증 시도.");
//    }


    @Getter
    @ToString
    @AllArgsConstructor

    public static class ExceptionResponse {
        private String message;
        private ErrorCode errorCode;
        private int status;
    }
}
