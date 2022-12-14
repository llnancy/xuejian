package io.github.llnancy.xuejian.configure;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * SpringBoot 配置属性 启动器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/13
 */
@SpringBootApplication
public class XueJianConfigureApplication {

    public static void main(String[] args) {
        // SpringApplication.run(XueJianConfigureApplication.class, args);
        new SpringApplicationBuilder(XueJianConfigureApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args); // 添加临时参数args
    }
}
