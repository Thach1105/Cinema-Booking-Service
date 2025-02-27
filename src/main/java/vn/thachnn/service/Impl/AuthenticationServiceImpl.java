package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.thachnn.common.TokenType;
import vn.thachnn.common.UserStatus;
import vn.thachnn.dto.request.SignInRequest;
import vn.thachnn.dto.response.TokenResponse;
import vn.thachnn.exception.*;
import vn.thachnn.model.BlackListToken;
import vn.thachnn.model.EmailCode;
import vn.thachnn.model.User;
import vn.thachnn.repository.BlackListTokenRepository;
import vn.thachnn.repository.EmailCodeRepository;
import vn.thachnn.repository.UserRepository;
import vn.thachnn.service.AuthenticationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmailCodeRepository emailCodeRepository;
    private final JwtServiceImpl jwtService;
    private final BlackListTokenRepository blackListTokenRepository;

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
            throw new BadCredentialsException("Username or password is incorrect");
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
    @Transactional(rollbackFor = Exception.class)
    public void signOut(String accessToken, String refreshToken, User user) {
        log.info("Sign out account with username: {}", user.getUsername());
        String usernameFromRT = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);

        if(!Objects.equals(user.getUsername(), usernameFromRT)){
            throw new BadRequestException("");
        }

        BlackListToken blackListRT = BlackListToken.builder()
                .id(jwtService.extractTokenId(refreshToken, TokenType.REFRESH_TOKEN))
                .token(refreshToken)
                .type(TokenType.REFRESH_TOKEN)
                .createdAt(LocalDateTime.now())
                .expiredAt(jwtService.extractExpiration(refreshToken, TokenType.REFRESH_TOKEN))
                .reason("SIGN-OUT")
                .build();

        BlackListToken blackListAT = BlackListToken.builder()
                .id(jwtService.extractTokenId(accessToken, TokenType.ACCESS_TOKEN))
                .token(accessToken)
                .type(TokenType.ACCESS_TOKEN)
                .createdAt(LocalDateTime.now())
                .expiredAt(jwtService.extractExpiration(accessToken, TokenType.ACCESS_TOKEN))
                .reason("SIGN-OUT")
                .build();

        blackListTokenRepository.saveAll(List.of(blackListAT, blackListRT));

        //clear the authentication from SecurityContextHolder to log out the user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            SecurityContextHolder.clearContext();
            log.info("User {} has been logged out successfully", user.getId());
        }
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
            throw new BadRequestException("Email verification failed");
        }
    }
}
