package com.sunchaser.oxygen.apollo;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/27
 */
@SpringBootApplication
public class ApolloApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ApolloApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
