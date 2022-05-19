package com.sunchaser.chunyu.junit;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/19
 */
@SpringBootApplication
public class ChunYuJunitApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ChunYuJunitApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
