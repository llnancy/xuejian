package com.sunchaser.oxygen.rabbitmq.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 属性配置信息类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Configuration
@ConfigurationProperties(prefix = "my.rabbitmq")
@Data
public class RabbitMQProperties {

    /**
     * 交换机名称
     */
    private String topicExchangeName;

    /**
     * 队列名称
     */
    private String topicQueueName;

    /**
     * 绑定键
     */
    private String topicRoutingKey;
}
