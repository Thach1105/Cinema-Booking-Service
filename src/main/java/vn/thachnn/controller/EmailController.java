package vn.thachnn.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.thachnn.service.EmailService;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-CONTROLLER")
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/send-email")
    public void send(@RequestParam String to,
                     @RequestParam String subject,
                     @RequestParam String text)
    {
        log.info("Sending email to: {}", to);
        emailService.sendMail(to, subject, text);
    }

    @GetMapping("/send-verify-email")
    public void verifyEmail(
            @RequestParam String to,
            @RequestParam String username,
            @RequestParam String fullName
    ){
        try {
            log.info("Sending verify email to: {}", to);
            emailService.sendVerificationEmail(to, username, fullName);
        } catch (Exception e) {
            log.info("Failed to send verification email");
            throw new RuntimeException(e.getMessage());
        }
    }
}
