package com.sunchaser.oxygen.rabbitmq.mq.consumer;

import com.rabbitmq.client.Channel;
import com.sunchaser.oxygen.rabbitmq.model.MsgDTO;
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
@RabbitListener(queues = "${my.rabbitmq.topic-queue-name}")
@Slf4j
public class BootTopicQueueListener {

    @RabbitHandler
    public void listener(String msg, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("RabbitMQ - [BootTopicQueueListener] 消费端 消费到消息，msg={}, message={}", msg, message);
            if ("error".equals(msg)) {
                throw new RuntimeException("error");
            }
            // 手动ack，肯定应答，multiple=false表示不批量
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("RabbitMQ - [BootTopicQueueListener] 消费端 消费消息时发生异常", e);
            try {
                // 否定应答，multiple=false表示不批量，requeue=false表示不重新投递至队列（即丢弃）
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ex) {
                log.error("RabbitMQ - [BootTopicQueueListener] 消费端 否定应答消息时发生异常", ex);
            }
        }
    }

    @RabbitHandler
    public void listener(MsgDTO msg, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("RabbitMQ - [BootTopicQueueListener] 消费端 消费到消息，msg={}, message={}", msg, message);
            // 手动ack，肯定应答，multiple=false表示不批量
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("RabbitMQ - [BootTopicQueueListener] 消费端 消费消息时发生异常", e);
            try {
                // 否定应答，multiple=false表示不批量，requeue=false表示不重新投递至队列（即丢弃）
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ex) {
                log.error("RabbitMQ - [BootTopicQueueListener] 消费端 否定应答消息时发生异常", ex);
            }
        }
    }
}
