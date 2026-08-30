package br.com.ferrickharmony.notification.service;

import br.com.ferrickharmony.notification.dto.EmailDTO;
import br.com.ferrickharmony.notification.enums.StatusEmail;
import br.com.ferrickharmony.notification.model.Email;
import br.com.ferrickharmony.notification.repository.EmailRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Transactional
    public void sendEmail(EmailDTO email) {
        String emailBody = email.text();
        if (email.appointmentEmail() != null && email.variables() != null) {
            Context context = new Context();
            context.setVariables(email.variables());
            emailBody = templateEngine.process(email.appointmentEmail(), context);
        }

        Email emailModel = Email.builder()
                .userId(email.userId())
                .emailTo(email.emailTo())
                .subject(email.subject())
                .text(emailBody)
                .emailFrom(emailFrom)
                .sendDateEmail(LocalDateTime.now())
                .build();

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailFrom);
            helper.setTo(email.emailTo());
            helper.setSubject(email.subject());
            helper.setText(emailBody, true);

            emailSender.send(message);
            emailModel.setStatusEmail(StatusEmail.SENT);
        } catch (Exception e) {
            emailModel.setStatusEmail(StatusEmail.ERROR);
        } finally {
            emailRepository.save(emailModel);
        }
    }
}