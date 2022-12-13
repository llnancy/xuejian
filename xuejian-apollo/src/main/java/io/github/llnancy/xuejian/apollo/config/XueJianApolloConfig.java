package io.github.llnancy.xuejian.apollo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * apollo config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/28
 */
@ConfigurationProperties(prefix = "xuejian.apollo")
@Configuration
@Getter
@Setter
public class XueJianApolloConfig {

    private String name;

    private List<String> jsonstr;
}
