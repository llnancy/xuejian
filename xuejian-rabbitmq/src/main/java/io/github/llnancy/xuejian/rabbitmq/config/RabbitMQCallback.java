package io.github.llnancy.xuejian.rabbitmq.config;

import io.github.llnancy.xuejian.rabbitmq.config.property.RabbitMQProperties;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * RabbitMQ 消息可靠投递 生产者发布确认回调和消息回退回调
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Configuration
@Slf4j
public class RabbitMQCallback implements InitializingBean, RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    /**
     * 生产者发布确认回调
     * 需要开启 spring.rabbitmq.publisher-confirm-type 参数，建议设置为 correlated，异步回调。
     *
     * @param correlationData 可保存消息ID和消息内容，需要发送方在消息发送时自行设置
     * @param ack             true：交换机收到消息；false：交换机未收到消息
     * @param cause           投递到交换机失败的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = Objects.isNull(correlationData) ? "" : correlationData.getId();
        if (ack) {
            log.info("RabbitMQ - [ConfirmCallback] 生产端 ID={}的消息投递至交换机-成功", id);
        } else {
            log.error("RabbitMQ - [ConfirmCallback] 生产端 ID={}的消息投递至交换机-失败，原因：{}", id, cause);
        }
    }

    /**
     * 交换机路由消息至队列失败 - 消息回退回调
     * 需要开启 spring.rabbitmq.publisher-returns 参数，设置为 true。
     * 同时需要配合 spring.rabbitmq.template.mandatory 参数使用，也设置为 true。
     *
     * @param returned 退回的消息
     */
    @Override
    public void returnedMessage(@NonNull ReturnedMessage returned) {
        if (rabbitMQProperties.getDelayedExchangeName().equals(returned.getExchange())) {
            // 处理 rabbitmq-delayed-message-exchange 插件的延迟交换机回调
            return;
        }
        log.error("RabbitMQ - [ReturnsCallback] 交换机路由消息至队列失败，消息退回发起者，消息内容为：{}", returned);
    }
}
