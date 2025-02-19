package vn.thachnn.service;

import vn.thachnn.dto.request.SignInRequest;
import vn.thachnn.dto.response.TokenResponse;

public interface AuthenticationService {

    TokenResponse signIn(SignInRequest request) throws Exception;

    TokenResponse refreshToken(String refreshToken);

    void confirmEmail(String email, String secretCode);
}
