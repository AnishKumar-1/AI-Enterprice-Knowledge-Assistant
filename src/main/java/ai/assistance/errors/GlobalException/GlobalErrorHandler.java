package ai.assistance.errors.GlobalException;

import ai.assistance.errors.custom.LlmRateLimitExceededException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    //handle method level error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        System.out.println("handleMethodArgumentNotValidException "+  ex.getMessage());
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError.getDefaultMessage();

        CustomErrorResponse customErrorResponse = CustomErrorResponse.builder()
                .error(new CustomErrorResponse.Error(HttpStatus.BAD_REQUEST.toString(), message))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> UsernameNotFoundExceptionHandler(UsernameNotFoundException ex, HttpServletRequest request) {
        CustomErrorResponse customErrorResponse = CustomErrorResponse.builder()
                .error(new CustomErrorResponse.Error(HttpStatus.NOT_FOUND.toString(), ex.getLocalizedMessage()))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(customErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<CustomErrorResponse> handleExpiredJwtException(ExpiredJwtException ex, HttpServletRequest request) {
        CustomErrorResponse customErrorResponse = CustomErrorResponse.builder()
                .error(new CustomErrorResponse.Error(HttpStatus.
                        BAD_REQUEST.toString(), "Jwt token has expired."))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<CustomErrorResponse> handleMalformedJwtException(MalformedJwtException ex, HttpServletRequest request) {
        CustomErrorResponse customErrorResponse = CustomErrorResponse.builder()
                .error(new CustomErrorResponse.Error(HttpStatus.
                        BAD_REQUEST.toString(), "Jwt token is changed or invalid"))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> otherException(Exception ex, HttpServletRequest request) {
        log.error("Exception occurred: {}", ex);
        CustomErrorResponse customErrorResponse = CustomErrorResponse.builder()
                .error(new CustomErrorResponse.Error(HttpStatus.BAD_REQUEST.toString(), ex.getLocalizedMessage()))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LlmRateLimitExceededException.class)
    public ResponseEntity<Map<String,String>> handleRateLimit(
            LlmRateLimitExceededException ex) {

        Map<String,String> errorMessage=new HashMap<>();
        errorMessage.put("message",ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(errorMessage);
    }

}
