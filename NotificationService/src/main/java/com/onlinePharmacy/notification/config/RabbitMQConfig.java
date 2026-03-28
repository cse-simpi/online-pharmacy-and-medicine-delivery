package com.onlinePharmacy.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    /**
     * The pharmacy.events exchange is already declared by admin-service.
     * Declaring it again here is safe — RabbitMQ is idempotent on re-declare
     * with the same arguments. This ensures the service works standalone too.
     */
    @Bean
    public TopicExchange pharmacyEventsExchange() {
        return new TopicExchange(RabbitMQConstants.EXCHANGE, true, false);
    }

    /**
     * New queue exclusively for notification-service.
     * Backed by DLQ so failed email deliveries don't get lost.
     */
    @Bean
    public Queue notificationOrderPlacedQueue() {
        return QueueBuilder
                .durable(RabbitMQConstants.Q_NOTIFICATION_ORDER_PLACED)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DLX)
                .build();
    }

    /**
     * Bind the new queue to the existing exchange with the same routing key
     * order-service already publishes. No change to order-service needed.
     */
    @Bean
    public Binding notificationOrderPlacedBinding() {
        return BindingBuilder
                .bind(notificationOrderPlacedQueue())
                .to(pharmacyEventsExchange())
                .with(RabbitMQConstants.RK_ORDER_PLACED);
    }
}