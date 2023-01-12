package io.github.llnancy.xuejian.jpa;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * JPA 启动类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/9
 */
@SpringBootApplication
// @EnableJpaRepositories(queryLookupStrategy = QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND)
public class XueJianJpaApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(XueJianJpaApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
