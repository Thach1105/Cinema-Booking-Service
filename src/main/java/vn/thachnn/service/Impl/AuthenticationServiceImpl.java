package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.thachnn.common.TokenType;
import vn.thachnn.common.UserStatus;
import vn.thachnn.dto.request.SignInRequest;
import vn.thachnn.dto.response.TokenResponse;
import vn.thachnn.exception.*;
import vn.thachnn.model.EmailCode;
import vn.thachnn.model.User;
import vn.thachnn.repository.EmailCodeRepository;
import vn.thachnn.repository.UserRepository;
import vn.thachnn.service.AuthenticationService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmailCodeRepository emailCodeRepository;
    private final JwtServiceImpl jwtService;

    @Override
    public TokenResponse signIn(SignInRequest request) throws Exception {
        log.info("Log in account with username {}", request.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            log.info("isAuthenticated: {}", authentication.isAuthenticated());
            log.info("Authorities: {}", authentication.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e){
            log.error("Login failed");
            throw new AppException(ErrorApp.INCORRECT_SIGNIN_REQ);
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        String accessToken = jwtService.generateAccessToken(user.getId(), request.getUsername(), user.getAuthorities());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), request.getUsername(), user.getAuthorities());
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        log.info("Get refresh token");

        if(!StringUtils.hasLength(refreshToken)){
            throw new InvalidDataException("Token must be not blank");
        }

        try {
            //verify token
            String username = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);

            //Check user is active or inactived
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            //generate new access token
            String accessToken = jwtService.generateAccessToken(user.getId(), username, user.getAuthorities());

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            log.error("Access denied! Error message: {}", e.getMessage());
            throw new ForbiddenException(e.getMessage());
        }
    }

    @Override
    public void confirmEmail(String email, String secretCode) {
        log.info("Verifying email :{}", email);

        // Find emailCode in redis
        EmailCode emailCode = emailCodeRepository.findById(secretCode)
                .orElseThrow(
                        () -> {
                            log.info("Verification failed");
                            return new ResourceNotFoundException("Secret Code not found");
                        }
                );


        // compare emails, if verification is successful and delete emailCode
        if(email.equals(emailCode.getEmail())){
            User user = userRepository.findByEmail(email)
                    .orElseThrow(
                            () -> new ResourceNotFoundException("User not found")
                    );

            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
            log.info("Email verified successfully");
            emailCodeRepository.delete(emailCode);
        } else {
            log.error("Verification failed, Emails do not match");
            throw new AppException(ErrorApp.VERIFY_EMAIL_FAILED);
        }
    }
}
