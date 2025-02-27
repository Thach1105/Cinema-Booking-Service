package vn.thachnn.service;

import vn.thachnn.dto.request.SignInRequest;
import vn.thachnn.dto.response.TokenResponse;
import vn.thachnn.model.User;

public interface AuthenticationService {

    TokenResponse signIn(SignInRequest request) throws Exception;

    TokenResponse refreshToken(String refreshToken);

    void confirmEmail(String email, String secretCode);

    void signOut(String accessToken, String refreshToken, User user);
}
