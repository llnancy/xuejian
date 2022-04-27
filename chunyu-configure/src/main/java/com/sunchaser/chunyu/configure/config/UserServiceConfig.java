package com.sunchaser.chunyu.configure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * user-service urls config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/14
 */
@Data
@Component
@ConfigurationProperties(prefix = "user-service")
public class UserServiceConfig {
    private String baseDomain;

    private Urls urls;

    @Data
    static class Urls {
        private String getUsers;
        private String addUser;
        private String updateUser;
    }
}
