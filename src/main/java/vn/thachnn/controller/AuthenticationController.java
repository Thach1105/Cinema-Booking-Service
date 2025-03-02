package vn.thachnn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.thachnn.dto.request.SignInRequest;
import vn.thachnn.dto.response.ApiResponse;
import vn.thachnn.model.User;
import vn.thachnn.service.Impl.AuthenticationServiceImpl;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationService;

    @Operation(summary = "Sign In", description = "Get access token and refresh token by username and password")
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(
            @RequestBody @Valid SignInRequest request
    ) throws Exception {
        log.info("Access token request");
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Sign in successful")
                .data(authenticationService.signIn(request))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "Refresh Token", description = "Get access token by refresh token")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @RequestParam String refreshToken
    ){
        log.info("Refresh token request");
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Refresh token successful")
                .data(authenticationService.refreshToken(refreshToken))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "Confirm Email", description = "Confirm email for account")
    @GetMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(
            @RequestParam @Email String email,
            @RequestParam String secretCode
    ){
        log.info("Confirm email: {}", email);
        authenticationService.confirmEmail(email, secretCode);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Email verified successfully")
                .data("")
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/sign-out")
    @Operation(
            summary = "Sign out user",
            description = "This API allows users to log out by providing an Access Token and Refresh Token. " +
                    "The access token should be sent in the Authorization header, prefixed with 'Bearer '."
    )
    public ResponseEntity<?> signOut(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String refreshToken,
            @AuthenticationPrincipal User user
    ){
        String accessToken = authHeader.substring("Bearer ".length());
        authenticationService.signOut(accessToken, refreshToken, user);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Successfully logged out")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
