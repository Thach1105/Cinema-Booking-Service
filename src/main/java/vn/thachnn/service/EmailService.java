package vn.thachnn.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vn.thachnn.model.EmailCode;
import vn.thachnn.repository.EmailCodeRepository;

import java.text.MessageFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-SERVICE")
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailCodeRepository emailCodeRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendMail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true để gửi HTML

            mailSender.send(message);
            log.info("Email sent successfully");
        } catch (MessagingException e) {
            log.error("Email sent failed, errorMessage= {}", e.getMessage());
        }
    }

    public void sendVerificationEmail(String to, String username, String fullName) throws Exception {
        String subject = "Verify user";

        // Generate secret code and save to db
        String secretCode = UUID.randomUUID().toString();
        log.info("Secret code = {}", secretCode);

        //Save secretCode to redisHash
        EmailCode emailCode = EmailCode.builder()
                .email(to)
                .id(secretCode)
                .build();
        emailCodeRepository.save(emailCode);
        log.info("Saved secret code for email {}", to);

        // Generate url verify
        String verificationLink = "http://localhost:8080/auth/confirm-email?email=%s&secretCode=%s".formatted(to, secretCode);
        String body = createVerificationEmailContent(username, to, verificationLink, fullName);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
        log.info("Verification sent successfully");
    }

    //create template e
    private String createVerificationEmailContent(String username, String email, String verificationLink, String name){
        String emailTemplate = """
                <html dir="ltr" xmlns="http://www.w3.org/1999/xhtml" xmlns:o="urn:schemas-microsoft-com:office:office">
                    <head>
                           <meta charset="UTF-8">
                           <meta content="width=device-width, initial-scale=1" name="viewport">
                           <meta name="x-apple-disable-message-reformatting">
                           <meta http-equiv="X-UA-Compatible" content="IE=edge">
                           <meta content="telephone=no" name="format-detection">
                           <title></title>
                           <link href="https://fonts.googleapis.com/css?family=Roboto:400,400i,700,700i" rel="stylesheet">
                    </head>
                     <body class="body">
                       <div dir="ltr" class="es-wrapper-color">
                         <table width="100%" cellspacing="0" cellpadding="0" class="es-wrapper">
                           <tbody>
                             <tr>
                               <td valign="top" class="esd-email-paddings">
                                 <table cellspacing="0" cellpadding="0" align="center" class="es-content">
                                   <tbody>
                                     <tr>
                                       <td align="center" class="esd-stripe">
                                         <table esd-img-prev-src="" width="600" cellspacing="0" cellpadding="0" align="center" class="es-content-body" style="background-color:transparent">
                                           <tbody>
                                             <tr>
                                               <td esd-img-prev-src="" align="left" class="esd-structure">
                                                 <table width="100%" cellspacing="0" cellpadding="0">
                                                   <tbody>
                                                     <tr>
                                                       <td width="600" valign="top" align="center" class="esd-container-frame">
                                                         <table esd-img-prev-src="" width="100%" cellspacing="0" cellpadding="0" bgcolor="#fcfcfc" style="border-radius:3px;border-collapse:separate;background-color:rgb(252, 252, 252)">
                                                           <tbody>
                                                             <tr>
                                                               <td align="left" class="esd-block-text es-p30t es-p20r es-p20l">
                                                                 <h2 class="es-m-txt-l" style="color:#333333">
                                                                   Welcome!
                                                                 </h2>
                                                               </td>
                                                             </tr>
                                                             <tr>
                                                               <td bgcolor="#fcfcfc" align="left" class="esd-block-text es-p10t es-p20r es-p20l">
                                                                 <p>
                                                                   Chào {0}, chúng tôi rất vui vì bạn ở đây! Bạn có thể tận hưởng việc xem phim và khám phá các chương trình giảm giá mới mỗi tuần. Trước tiên, hãy ấn vào nút <strong>Verify</strong> ở dưới để xác thực tài khoản người dùng của bạn<br>
                                                                 </p>
                                                               </td>
                                                             </tr>
                                                           </tbody>
                                                         </table>
                                                       </td>
                                                     </tr>
                                                   </tbody>
                                                 </table>
                                               </td>
                                             </tr>
                                             <tr>
                                               <td esd-img-prev-src="" bgcolor="#fcfcfc" align="left" class="esd-structure es-p30t es-p20r es-p20l" style="background-color:rgb(252, 252, 252)">
                                                 <table width="100%" cellspacing="0" cellpadding="0">
                                                   <tbody>
                                                     <tr>
                                                       <td width="560" valign="top" align="center" class="esd-container-frame">
                                                         <table esd-img-prev-src="" width="100%" cellspacing="0" cellpadding="0" bgcolor="#ffffff" style="border-color:rgb(239, 239, 239);border-style:solid;border-width:1px;border-radius:3px;border-collapse:separate;background-color:rgb(255, 255, 255)">
                                                           <tbody>
                                                             <tr>
                                                               <td align="center" class="esd-block-text es-p20t es-p15b">
                                                                 <h3 style="color:#333333">
                                                                   Your account information:
                                                                 </h3>
                                                               </td>
                                                             </tr>
                                                             <tr>
                                                               <td align="center" class="esd-block-text">
                                                                 <p style="color:rgb(100, 67, 74);font-size:16px;line-height:150%">
                                                                   Login: {1}
                                                                 </p>
                                                                 <p style="color:rgb(100, 67, 74);font-size:16px;line-height:150%">
                                                                   Email: {2}
                                                                 </p>
                                                               </td>
                                                             </tr>
                                                             <tr>
                                                               <td align="center" class="esd-block-button es-p20t es-p20b es-p10r es-p10l">
                                                                 <span class="es-button-border" style="background:rgb(248, 243, 239) none repeat scroll 0% 0%">
                                                                   <a href="{3}" target="_blank" class="es-button" style="background:rgb(248, 243, 239) none repeat scroll 0% 0%;border-color:rgb(248, 243, 239)">
                                                                     Verify
                                                                   </a>
                                                                 </span>
                                                               </td>
                                                             </tr>
                                                           </tbody>
                                                         </table>
                                                       </td>
                                                     </tr>
                                                   </tbody>
                                                 </table>
                                               </td>
                                             </tr>
                                           </tbody>
                                         </table>
                                       </td>
                                     </tr>
                                   </tbody>
                                 </table>
                               </td>
                             </tr>
                           </tbody>
                         </table>
                       </div>
                   </body>
                </html>
                """;
        return MessageFormat.format(emailTemplate, name, username, email, verificationLink);
    }
}
