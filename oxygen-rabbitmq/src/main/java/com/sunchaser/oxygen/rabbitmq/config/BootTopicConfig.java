package com.sunchaser.oxygen.rabbitmq.config;

import com.sunchaser.oxygen.rabbitmq.config.property.RabbitMQProperties;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 交换机、队列、binding关系 配置类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Configuration
public class BootTopicConfig {

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    @Bean
    public Exchange bootTopicExchange() {
        return ExchangeBuilder.topicExchange(rabbitMQProperties.getTopicExchangeName())
                .build();
    }

    @Bean
    public Queue bootQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getTopicQueueName())
                .build();
    }

    @Bean
    public Binding bindingBootQueueExchange() {
        return BindingBuilder.bind(bootQueue())
                .to(bootTopicExchange())
                .with(rabbitMQProperties.getTopicRoutingKey())
                .noargs();
    }
}
