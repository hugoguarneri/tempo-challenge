package com.tenpo.challenge.handler;

import com.tenpo.challenge.exception.AppException;
import com.tenpo.challenge.exception.RateLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AppException.class)
    public final ResponseEntity<Object> handleAppException(
            AppException ex,
            WebRequest request
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("message", "Failed to perform the operation.");
        body.put("error", ex.toString());

        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(RateLimitException.class)
    public final ResponseEntity<Object> handleRateLimitException(
            RateLimitException ex,
            WebRequest request
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        body.put("message", "Too many requests, please try again later.");
        body.put("error", ex.toString());

        return new ResponseEntity<>(body, HttpStatus.TOO_MANY_REQUESTS);
    }

}
