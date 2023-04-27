package com.bamdoliro.maru.infrastructure.mail;

import com.bamdoliro.maru.infrastructure.mail.exception.FailedToSendMailException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SendEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void execute(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            message.addRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(subject);
            message.setText(body, "utf-8", "html");

            message.setFrom(new InternetAddress(from, "밤돌이로"));

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FailedToSendMailException();
        }
    }
}
