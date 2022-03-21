package com.sunchaser.oxygen.junit;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/19
 */
@SpringBootApplication
public class JunitApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(JunitApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
