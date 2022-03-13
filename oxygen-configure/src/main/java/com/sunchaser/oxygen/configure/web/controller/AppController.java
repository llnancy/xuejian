package com.sunchaser.oxygen.configure.web.controller;

import com.sunchaser.oxygen.configure.config.UserServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 各种数据类型的属性注入
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/13
 */
@RestController
@Slf4j
public class AppController {
    @Value("${title}")
    private String title;

    @Value("${oxygen.user.name}")
    private String name;
    @Value("${oxygen.user.age}")
    private Integer age;

    @Value("${languages[0]}")
    private String language;
    @Value("${platforms[1]}")
    private String platform;

    @Value("${user-service.urls.get-users}")
    private String getUsersUrl;

    @Value("${escape-character}")
    private String escapeCharacter;

    @Value("${default-value:abc}")
    private String defaultV;

    @Autowired
    private Environment env;

    @Autowired
    private UserServiceConfig userServiceConfig;

    @GetMapping("/yml")
    public void yml() {
        // 单一属性注入
        log.info("title={}", title);
        // 多级属性注入
        log.info("oxygen.user.name={}", name);
        log.info("oxygen.user.age={}", age);
        // 数组类型注入
        log.info("language[0]={}", language);
        log.info("platform[1]={}", platform);
        // 变量引用
        log.info("user-service.urls.get-users={}", getUsersUrl);
        // 转义字符
        log.info("escape-character={}", escapeCharacter);
        // 默认值
        log.info("default-value={}", defaultV);

        // Environment
        log.info("Environment#oxygen.user.name={}", env.getProperty("oxygen.user.name"));
        log.info("Environment#languages[0]={}", env.getProperty("languages[0]"));
        log.info("user-service.urls.get-users={}", env.getProperty("user-service.urls.get-users"));
        log.info("default-value={}", env.getProperty("default-value", "env-abc"));

        // @ConfigurationProperties
        log.info("userServiceConfig={}", userServiceConfig);
    }
}
