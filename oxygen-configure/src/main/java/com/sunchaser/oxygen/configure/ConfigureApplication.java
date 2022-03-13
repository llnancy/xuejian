package com.sunchaser.oxygen.configure;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/13
 */
@SpringBootApplication
public class ConfigureApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ConfigureApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
