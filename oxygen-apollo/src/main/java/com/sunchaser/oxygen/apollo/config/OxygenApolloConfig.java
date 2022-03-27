package com.sunchaser.oxygen.apollo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/28
 */
@ConfigurationProperties(prefix = "oxygen.apollo")
@Configuration
@Getter
@Setter
public class OxygenApolloConfig {
    private String name;
    private List<String> jsonstr;
}
