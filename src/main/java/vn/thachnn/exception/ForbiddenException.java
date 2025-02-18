package vn.thachnn.exception;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String mess){
        super(mess);
    }
}
