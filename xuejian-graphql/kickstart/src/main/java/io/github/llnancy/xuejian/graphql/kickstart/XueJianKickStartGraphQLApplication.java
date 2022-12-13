package io.github.llnancy.xuejian.graphql.kickstart;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * kickstart spring boot 启动类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/9
 */
@SpringBootApplication
public class XueJianKickStartGraphQLApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(XueJianKickStartGraphQLApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
