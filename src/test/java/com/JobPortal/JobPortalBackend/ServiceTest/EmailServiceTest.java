package com.JobPortal.JobPortalBackend.ServiceTest;


import com.JobPortal.JobPortalBackend.Services.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmail_ShouldSendEmailSuccessfully() {

        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        emailService.sendEmail(to, subject, body);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_ShouldSetCorrectEmailFields() {

        String to = "test@example.com";
        String subject = "Welcome";
        String body = "Hello User";

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendEmail(to, subject, body);

        verify(javaMailSender).send(captor.capture());

        SimpleMailMessage sentMail = captor.getValue();

        assertArrayEquals(new String[]{to}, sentMail.getTo());
        assertEquals(subject, sentMail.getSubject());
        assertEquals(body, sentMail.getText());
    }

    @Test
    void sendEmail_ShouldThrowRuntimeException_WhenMailSenderFails() {

        String to = "test@example.com";
        String subject = "Test";
        String body = "Body";

        RuntimeException originalException = new RuntimeException("Mail server down");

        doThrow(originalException).when(javaMailSender).send(any(SimpleMailMessage.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class, () -> emailService.sendEmail(to, subject, body));

        assertEquals(originalException, thrown.getCause());
    }
}