package io.github.llnancy.xuejian.rabbitmq.mq.producer;

import io.github.llnancy.xuejian.rabbitmq.config.property.RabbitMQProperties;
import io.github.llnancy.xuejian.rabbitmq.model.MsgDTO;
import org.springframework.amqp.core.MessageProperties;
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
public class RabbitMQProducer {

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

    public void send(MsgDTO msgDTO, String routingKey) {
        rabbitTemplate.convertAndSend(
                rabbitMQProperties.getTopicExchangeName(),
                routingKey,
                msgDTO,
                new CorrelationData()
        );
    }

    public void send(MsgDTO msgDTO, String routingKey, String expire) {
        rabbitTemplate.convertAndSend(
                rabbitMQProperties.getNormalExchangeName(),
                routingKey,
                msgDTO,
                message -> {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setExpiration(expire);// 消息过期时间，单位ms。
                    return message;
                },
                new CorrelationData()
        );
    }

    public void sendNormalExchange(MsgDTO msgDTO, String routingKey) {
        rabbitTemplate.convertAndSend(
                rabbitMQProperties.getNormalExchangeName(),
                routingKey,
                msgDTO,
                new CorrelationData()
        );
    }

    public void send(MsgDTO msgDTO, String routingKey, Integer delay) {
        rabbitTemplate.convertAndSend(
                rabbitMQProperties.getDelayedExchangeName(),
                routingKey,
                msgDTO,
                message -> {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setDelay(delay); // 消息延迟时间，单位ms。
                    messageProperties.setPriority(5);
                    return message;
                },
                new CorrelationData()
        );
    }
}
