package com.sunchaser.oxygen.mybatisplus;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/10
 */
@SpringBootApplication
public class MybatisPlusApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MybatisPlusApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
