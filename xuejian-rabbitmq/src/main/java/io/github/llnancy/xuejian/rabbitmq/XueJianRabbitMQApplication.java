package io.github.llnancy.xuejian.rabbitmq;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * rabbitmq 启动类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@SpringBootApplication
public class XueJianRabbitMQApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(XueJianRabbitMQApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
