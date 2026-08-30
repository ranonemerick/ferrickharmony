package br.com.ferrickharmony.producer;

import br.com.ferrickharmony.dto.email.EmailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${broker.exchange.appointment.name}")
    private String exchangeName;

    @Value("${broker.routing-key.email.name}")
    private String routingKey;

    public void publishEmailMessage(EmailDTO email) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, email);
    }

}
