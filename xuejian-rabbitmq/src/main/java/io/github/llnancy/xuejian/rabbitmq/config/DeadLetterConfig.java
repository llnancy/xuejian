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
 * 死信交换机与队列配置
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/27
 */
@Configuration
public class DeadLetterConfig {

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    /**
     * 死信交换机
     *
     * @return TopicExchange
     */
    @Bean
    public Exchange deadLetterExchange() {
        return ExchangeBuilder.topicExchange(rabbitMQProperties.getDeadLetterExchangeName())
                .build();
    }

    /**
     * 死信队列
     *
     * @return Queue
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getDeadLetterQueueName())
                .build();
    }

    /**
     * 死信交换机和死信队列进行绑定
     *
     * @return Binding
     */
    @Bean
    public Binding bindingDeadQueueExchange() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(rabbitMQProperties.getDeadLetterRoutingKey())
                .noargs();
    }
}
