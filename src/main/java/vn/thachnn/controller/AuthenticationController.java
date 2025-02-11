package vn.thachnn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.thachnn.dto.response.ApiResponse;
import vn.thachnn.service.Impl.AuthenticationServiceImpl;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationService;

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
}
