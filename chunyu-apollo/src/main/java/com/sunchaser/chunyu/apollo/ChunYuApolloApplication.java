package com.sunchaser.chunyu.apollo;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/27
 */
@SpringBootApplication
public class ChunYuApolloApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ChunYuApolloApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
