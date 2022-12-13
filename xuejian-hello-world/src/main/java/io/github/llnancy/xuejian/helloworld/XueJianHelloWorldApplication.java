package io.github.llnancy.xuejian.helloworld;

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
public class XueJianHelloWorldApplication {

    public static void main(String[] args) {
        // SpringApplication.run(XueJianHelloWorldApplication.class, args);
        new SpringApplicationBuilder(XueJianHelloWorldApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
