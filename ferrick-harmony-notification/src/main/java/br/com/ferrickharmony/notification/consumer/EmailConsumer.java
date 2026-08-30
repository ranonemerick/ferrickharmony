package br.com.ferrickharmony.notification.consumer;

import br.com.ferrickharmony.notification.dto.EmailDTO;
import br.com.ferrickharmony.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "${broker.queue.email.name}")
    public void listenEmailQueue(@Payload EmailDTO email) {
        emailService.sendEmail(email);
        log.info("Sending email: {}", email.emailTo());
    }


}
