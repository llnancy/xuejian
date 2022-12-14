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
 * RabbitMQ 交换机、队列、binding 关系 配置类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Configuration
public class BootTopicConfig {

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    /**
     * 创建主题交换机。
     * ExchangeBuilder 中默认 durable=true，持久化。
     *
     * @return TopicExchange
     * @see org.springframework.amqp.core.TopicExchange
     */
    @Bean
    public Exchange bootTopicExchange() {
        return ExchangeBuilder.topicExchange(rabbitMQProperties.getTopicExchangeName())
                .build();
    }

    /**
     * 创建队列。
     * durable 方法创建的是持久化队列
     *
     * @return Queue
     */
    @Bean
    public Queue bootQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getTopicQueueName())
                .build();
    }

    /**
     * 交换机和队列进行绑定
     *
     * @return Binding
     */
    @Bean
    public Binding bindingBootQueueExchange() {
        return BindingBuilder.bind(bootQueue())
                .to(bootTopicExchange())
                .with(rabbitMQProperties.getTopicRoutingKey())
                .noargs();
    }
}
