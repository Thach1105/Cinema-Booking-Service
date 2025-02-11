package vn.thachnn.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException{

    private ErrorApp errorApp;

    public AppException(ErrorApp errorApp){
        this.errorApp = errorApp;
    }
}
