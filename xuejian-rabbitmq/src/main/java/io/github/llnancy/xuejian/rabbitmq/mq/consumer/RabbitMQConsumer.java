package io.github.llnancy.xuejian.rabbitmq.mq.consumer;

import com.rabbitmq.client.Channel;
import io.github.llnancy.xuejian.rabbitmq.model.MsgDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 消息消费者
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Component
@RabbitListener(queues = {
        "${my.rabbitmq.topic-queue-name}",
        "${my.rabbitmq.normal-queue-name}",
        "${my.rabbitmq.dead-letter-queue-name}",
        "${my.rabbitmq.delayed-queue-name}"
})
@Slf4j
public class RabbitMQConsumer {

    @RabbitHandler
    public void listener(String msg, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("RabbitMQ - [RabbitMQConsumer] 消费端 消费到消息，msg={}, message={}", msg, message);
            if ("error".equals(msg)) {
                throw new RuntimeException("error");
            }
            // 手动 ack，肯定应答，multiple=false 表示不批量
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("RabbitMQ - [RabbitMQConsumer] 消费端 消费消息时发生异常", e);
            try {
                // 否定应答，multiple=false 表示不批量，requeue=false 表示不重新投递至原队列
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ex) {
                log.error("RabbitMQ - [RabbitMQConsumer] 消费端 否定应答消息时发生异常", ex);
            }
        }
    }

    @RabbitHandler
    public void listener(MsgDTO msg, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("RabbitMQ - [RabbitMQConsumer] 消费端 消费到消息，msg={}, message={}", msg, message);
            if ("error".equals(msg.getMsg())) {
                throw new RuntimeException("error");
            }
            // 手动 ack，肯定应答，multiple=false 表示不批量
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("RabbitMQ - [RabbitMQConsumer] 消费端 消费消息时发生异常", e);
            try {
                // 否定应答，multiple=false 表示不批量，requeue=false 表示不重新投递至原队列，如果配置了死信则会转发到死信交换机。
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ex) {
                log.error("RabbitMQ - [RabbitMQConsumer] 消费端 否定应答消息时发生异常", ex);
            }
        }
    }
}
