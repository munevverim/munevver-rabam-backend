package com.munevver.rabam.event.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.audit-queue}")
    private String auditQueueName;

    @Value("${app.rabbitmq.audit-routing-key}")
    private String auditRoutingKey;

    @Bean
    public TopicExchange rabamExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable(auditQueueName).build();
    }

    @Bean
    public Binding auditBinding() {
        return BindingBuilder
                .bind(auditQueue())
                .to(rabamExchange())
                .with(auditRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}