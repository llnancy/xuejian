package com.sunchaser.chunyu.rabbitmq.config;

import com.google.common.collect.Maps;
import com.sunchaser.chunyu.rabbitmq.config.property.RabbitMQProperties;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 基于rabbitmq-delayed-message-exchange插件的延迟消息配置类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/5
 */
@Configuration
public class DelayedMessageConfig {

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    /**
     * x-delayed-message类型的交换机
     *
     * @return CustomExchange
     */
    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> arguments = Maps.newHashMapWithExpectedSize(1);
        arguments.put("x-delayed-type", ExchangeTypes.TOPIC);
        return new CustomExchange(rabbitMQProperties.getDelayedExchangeName(), "x-delayed-message", true, false, arguments);
    }

    /**
     * 延迟队列
     *
     * @return Queue
     */
    @Bean
    public Queue delayedQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getDelayedQueueName())
                .build();
    }

    /**
     * 延迟交换机和队列进行绑定
     *
     * @return Binding
     */
    @Bean
    public Binding bindingDelayedQueueExchange() {
        return BindingBuilder.bind(delayedQueue())
                .to(delayedExchange())
                .with(rabbitMQProperties.getDelayedRoutingKey())
                .noargs();
    }
}
