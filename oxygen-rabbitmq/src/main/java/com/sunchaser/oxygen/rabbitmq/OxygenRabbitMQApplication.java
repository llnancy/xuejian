package com.sunchaser.oxygen.rabbitmq;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@SpringBootApplication
public class OxygenRabbitMQApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(OxygenRabbitMQApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
