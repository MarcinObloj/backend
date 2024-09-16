package com.example.cv2.service;

import com.example.cv2.model.User;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(User user, String siteURL) {
        try {
            String subject = "Potwierdź swoje konto";
            String senderName = "CV Creator";
            String mailContent = "<html>"
                    + "<head>"
                    + "<style>"
                    + "  .email-container { font-family: Arial, sans-serif; font-size: 1.2rem; line-height: 1.6; color: #333333; padding: 20px; }"
                    + "  .email-header { font-size: 2rem; margin-bottom: 20px; }"
                    + "  .email-content { margin-bottom: 30px; }"
                    + "  .btn-primary { color:#171b21; font-size: 1.6rem; display: flex; justify-content: center; align-items: center; padding: 1em 2em; border-radius: 1.6rem; width: 40%; background: #fec85b; color: #ffffff; text-align: center; font-weight: bold; margin: 2em auto; transition: background-color 0.3s; text-decoration: none; }"
                    + "  .btn-primary:hover { background-color: #d5a84c; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class='email-container'>"
                    + "<h2 class='email-header'>Witaj, <strong>" + user.getUsername() + "</strong></h2>"
                    + "<div class='email-content'>"
                    + "<p>Kliknij w poniższy link, aby potwierdzić swoje konto:</p>"
                    + "<h3><a href='" + siteURL + "/verify?code=" + user.getVerificationToken() + "' class='btn-primary'>Potwierdź</a></h3>"
                    + "</div>"
                    + "<p>Dziękujemy!<br>Ekipa CV Creator <span>&#128512;</span></p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            sendEmail(user.getEmail(), subject, mailContent);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Problem with sending email", e);
        }
    }

    public void sendPasswordResetEmail(User user, String siteURL) {
        try {
            String subject = "Reset hasła";
            String senderName = "CV Creator";
            String mailContent = "<p>Witaj, " + "<strong>"+ user.getUsername() +"</strong>" + "</p>"
                    + "<p>Kliknij w poniższy link, aby zresetować hasło:</p>"
                    + "<h3><a href='" + siteURL + "'>Resetuj hasło</a></h3>"
                    + "<p>Jeśli nie prosiłeś o reset hasła, zignoruj tę wiadomość.</p>"
                    + "<p>Dziękujemy!<br>Ekipa CV Creator \uD83D\uDE00</p>";

            sendEmail(user.getEmail(), subject, mailContent);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Problem with sending email", e);
        }
    }

    private void sendEmail(String recipientEmail, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("localhost", "CV Creator");
        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
}
