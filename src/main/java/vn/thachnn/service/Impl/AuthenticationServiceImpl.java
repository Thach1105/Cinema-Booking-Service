package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.thachnn.common.UserStatus;
import vn.thachnn.exception.AppException;
import vn.thachnn.exception.ErrorApp;
import vn.thachnn.exception.ResourceNotFoundException;
import vn.thachnn.model.EmailCode;
import vn.thachnn.model.User;
import vn.thachnn.repository.EmailCodeRepository;
import vn.thachnn.repository.UserRepository;
import vn.thachnn.service.AuthenticationService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final EmailCodeRepository emailCodeRepository;

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
