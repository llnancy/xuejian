package io.github.llnancy.xuejian.apollo;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * apollo 启动器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/27
 */
@SpringBootApplication
public class XueJianApolloApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(XueJianApolloApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
