package ru.viterg.proselyte.stocksfeed.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    /**
     * Sends an activation email to the specified email address with the activation key.
     *
     * @param email         the email address to send the activation email to.
     * @param activationKey the activation key to include in the email.
     */
    public void sendActivationMail(String email, String activationKey) {
        log.debug("Sending activation email to '{}'", email);
        sendEmail(email, "Registration confirmation", activationKey, false, false);
    }

    private void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                  isMultipart, isHtml, to, subject, content);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, UTF_8.name());
            message.setTo(to);
            message.setFrom("Stocks Feed <noreply@stocks-feed>");
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            log.warn("Email couldn't be sent to user '{}'", to, e);
            throw new EmailNotSentException(e);
        }
    }
}
