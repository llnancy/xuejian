package io.github.llnancy.xuejian.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * RabbitMQ 基本配置类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Configuration
@Slf4j
public class RabbitMQConfig {

    /**
     * 消息内容使用 JSON 序列化和反序列化
     * 消息生产端 RabbitTemplate 会通过 RabbitTemplateConfigurer 自动注入该 Bean
     * 消息消费端 SimpleRabbitListenerContainerFactory 会自动注入该 Bean
     *
     * @return {@link Jackson2JsonMessageConverter}
     * @see org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
     * @see org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer
     * @see org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 不推荐使用下面这种 @Bean 注解方式自行注入 RabbitTemplate 对象的写法，除非需要存在两个不同配置项的 RabbitTemplate Bean
     * 原因是 new 关键字创建的 RabbitTemplate 对象无法使用 spring.rabbitmq.template.* 配置项
     * 优雅的写法请查看 io.github.llnancy.xuejian.rabbitmq.config.RabbitMQCallback 类
     *
     * @see RabbitMQCallback
     */
    // @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 消息生产端设置Jackson2JsonMessageConverter
        rabbitTemplate.setMessageConverter(messageConverter());

        /*
         * 需要开启 spring.rabbitmq.publisher-confirm-type 参数，建议设置为 correlated，异步回调。
         *
         * @param correlationData 可保存消息 ID 和消息内容，需要发送方在消息发送时自行设置
         * @param ack             true：交换机收到消息；false：交换机未收到消息
         * @param cause           投递到交换机失败的原因
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            String id = Objects.isNull(correlationData) ? "" : correlationData.getId();
            if (ack) {
                log.info("RabbitMQ - [ConfirmCallback] 生产端ID={}的消息投递至交换机 成功", id);
            } else {
                log.error("RabbitMQ - [ConfirmCallback] 生产端ID={}的消息投递至交换机 失败，原因：{}", id, cause);
            }
        });
        // 开启 mandatory，强制触发 ReturnsCallback 回调
        // rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(returned -> log.error("RabbitMQ - [ReturnsCallback] 交换机路由消息至队列失败，消息退回发起者，消息内容为：{}", returned));
        return rabbitTemplate;
    }
}
