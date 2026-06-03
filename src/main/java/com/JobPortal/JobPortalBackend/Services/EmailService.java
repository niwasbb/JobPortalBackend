package com.JobPortal.JobPortalBackend.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Async
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    public void sendEmail(String to,String subject, String body){
        log.info("Attempting to send email to: {} with subject: {}", to, subject);
        try{
            SimpleMailMessage mail=new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);

            javaMailSender.send(mail);

            log.info("Email sent successfully to: {}", to);

        } catch (Exception e) {
//            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
