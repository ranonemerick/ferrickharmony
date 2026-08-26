package br.com.ferrickharmony.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@SuppressWarnings({"removal"})
public class RabbitMQConfig {

    public static final String QUEUE_EMAIL = "appointment.email.queue";
    public static final String EXCHANGE_NAME = "appointment.exchange";
    public static final String ROUTING_KEY_EMAIL = "appointment.email.routingKey";

    @Bean
    public Queue emailQueue() {
        return new Queue(QUEUE_EMAIL, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding bindEmailQueue(Queue emailQueue, DirectExchange appointmentExchange) {
        return BindingBuilder.bind(emailQueue).to(appointmentExchange).with(ROUTING_KEY_EMAIL);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        return new Jackson2JsonMessageConverter(objectMapper);
    }

}
