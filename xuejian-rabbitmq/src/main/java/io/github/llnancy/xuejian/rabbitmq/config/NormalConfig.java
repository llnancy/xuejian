package io.github.llnancy.xuejian.rabbitmq.config;

import io.github.llnancy.xuejian.rabbitmq.config.property.RabbitMQProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 死信队列 - 正常收发消息的交换机与队列关系配置
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/27
 */
@Configuration
public class NormalConfig {

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    /**
     * 正常交换机。
     *
     * @return TopicExchange
     */
    @Bean
    public Exchange normalExchange() {
        return ExchangeBuilder.topicExchange(rabbitMQProperties.getNormalExchangeName())
                .build();
    }

    /**
     * 创建队列。
     * durable 方法创建的是持久化队列
     * deadLetterExchange 方法设置死信交换机
     * deadLetterRoutingKey 方法设置死信队列绑定键
     * maxLength 方法设置队列长度
     * ttl 方法设置队列延迟时间
     * maxPriority 方法设置优先队列
     *
     * @return Queue
     */
    @Bean
    public Queue normalQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getNormalQueueName())
                .deadLetterExchange(rabbitMQProperties.getDeadLetterExchangeName())
                .deadLetterRoutingKey(rabbitMQProperties.getDeadLetterRoutingKey())
                .maxLength(6)
                // .ttl(10000) // TTL：单位毫秒
                // .maxPriority(10) // 优先级：0~255范围内
                .build();
    }

    /**
     * 正常交换机和队列进行绑定
     *
     * @return Binding
     */
    @Bean
    public Binding bindingNormalQueueExchange() {
        return BindingBuilder.bind(normalQueue())
                .to(normalExchange())
                .with(rabbitMQProperties.getNormalRoutingKey())
                .noargs();
    }
}
