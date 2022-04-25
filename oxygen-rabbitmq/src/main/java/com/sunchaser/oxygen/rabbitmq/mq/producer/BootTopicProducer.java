package com.sunchaser.oxygen.rabbitmq.mq.producer;

import com.sunchaser.oxygen.rabbitmq.config.property.RabbitMQProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * topic交换机 消息生产者
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Component
public class BootTopicProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    public void send(String msg, String routingKey) {
        rabbitTemplate.convertAndSend(
                rabbitMQProperties.getTopicExchangeName(),
                routingKey,
                msg,
                new CorrelationData()
        );
    }
}
