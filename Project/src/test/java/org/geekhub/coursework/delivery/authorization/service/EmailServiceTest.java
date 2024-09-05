package org.geekhub.coursework.delivery.authorization.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private EmailService emailService;
    @Mock
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(javaMailSender);
    }

    @Test
    void sendSimpleMessage_shouldSendEmailCorrectly() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Message";

        emailService.sendSimpleMessage(to, subject, text);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
