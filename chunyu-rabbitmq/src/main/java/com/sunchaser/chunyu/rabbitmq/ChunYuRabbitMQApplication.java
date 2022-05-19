package com.sunchaser.chunyu.rabbitmq;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@SpringBootApplication
public class ChunYuRabbitMQApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ChunYuRabbitMQApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
