package br.com.ferrickharmony.notification.consumer;

import br.com.ferrickharmony.notification.dto.EmailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailConsumer {

    @RabbitListener(queues = "${broker.queue.email.name}")
    public void listenEmailQueue(@Payload EmailDTO email) {
        log.info("NOVA MENSAGEM RECEBIDA DA FILA!");
        log.info("Sending email: {}", email.to());
    }


}
