package com.sunchaser.oxygen.helloworld;

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
public class OxygenHelloWorldApplication {
    public static void main(String[] args) {
        // SpringApplication.run(HelloWorldApplication.class, args);
        new SpringApplicationBuilder(OxygenHelloWorldApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
