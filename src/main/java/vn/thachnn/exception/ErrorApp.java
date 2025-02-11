package vn.thachnn.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorApp {

    VERIFY_EMAIL_FAILED("Email verification failed", HttpStatus.BAD_REQUEST)

    ;
    private final String message;
    private final HttpStatus status;

    ErrorApp(String message, HttpStatus status){
        this.message = message;
        this.status = status;
    }
}
