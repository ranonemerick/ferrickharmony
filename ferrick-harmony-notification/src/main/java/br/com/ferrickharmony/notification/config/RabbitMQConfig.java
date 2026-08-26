package br.com.ferrickharmony.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@SuppressWarnings({"removal"})
public class RabbitMQConfig {

    @Value("${broker.queue.email.name}")
    private String queue;

    @Value("${broker.exchange.appointment.name}")
    private String exchange;

    @Value("${broker.routing-key.email.name}")
    private String routingKey;

    @Bean
    public Queue emailQueue() {
        return new Queue(queue, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding bindEmailQueue(Queue emailQueue, DirectExchange appointmentExchange) {
        return BindingBuilder.bind(emailQueue).to(appointmentExchange).with(routingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        return new Jackson2JsonMessageConverter(objectMapper);
    }

}
