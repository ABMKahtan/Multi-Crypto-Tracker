package com.coinmonitor.app.service;

import com.coinmonitor.app.model.NotificationType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    public void sendEmailNotification(
            Integer price,
            String coinId,
            String currencyId,
            String emailToNotify,
            NotificationType notificationType,
            File file
    ) {
        LOGGER.info("Sending notification to " + emailToNotify);
        String coin = coinId.toUpperCase();
        String currency = currencyId.toUpperCase();

        String subject = notificationType.equals(NotificationType.HIGHER_THAN_MAX) ?
                "HIGH PRICE ALERT FOR: " + coin : "LOW PRICE ALERT FOR " + coin;


        String text = "Current price for " + coin + " is " + price + " " + currency;

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(emailToNotify);
            helper.setFrom("cryptopricenotify@gmail.com");
            helper.setSubject(subject);
            helper.setText(text);
            FileSystemResource attachementFile = new FileSystemResource(file);
            helper.addAttachment("chart_" + coinId + ".png", attachementFile);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Error sending message", e);
        }

        file.delete();
    }
}
