package com.sunchaser.chunyu.helloworld;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * hello world 项目启动类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/6
 */
@SpringBootApplication
public class ChunYuHelloWorldApplication {

    public static void main(String[] args) {
        // SpringApplication.run(ChunYuHelloWorldApplication.class, args);
        new SpringApplicationBuilder(ChunYuHelloWorldApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
