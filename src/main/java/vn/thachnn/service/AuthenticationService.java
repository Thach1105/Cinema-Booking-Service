package vn.thachnn.service;

public interface AuthenticationService {

    void confirmEmail(String email, String secretCode);
}
