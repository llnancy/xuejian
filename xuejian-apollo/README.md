- [Spring Boot 整合 Apollo 分布式配置中心](#spring-boot-整合-apollo-分布式配置中心)
- [创建工程](#创建工程)
- [基本配置](#基本配置)
- [添加配置](#添加配置)
- [读取配置](#读取配置)
  - [使用 @ApolloConfig 注解](#使用-apolloconfig-注解)
  - [使用 @ApolloJsonValue 注解](#使用-apollojsonvalue-注解)
  - [使用 @Value 注解](#使用-value-注解)
  - [配合 @ConfigurationProperties 注解使用](#配合-configurationproperties-注解使用)
  - [使用 @ApolloConfigChangeListener 注解](#使用-apolloconfigchangelistener-注解)

# Spring Boot 整合 Apollo 分布式配置中心

核心依赖

```xml
<!-- apollo客户端 -->
<dependency>
    <groupId>com.ctrip.framework.apollo</groupId>
    <artifactId>apollo-client</artifactId>
    <version>1.9.2</version>
</dependency>
```

# 创建工程

创建 `Spring Boot` 项目 `xuejian-apollo`，引入 `apollo` 客户端及 `web` 依赖，完整 `pom.xml` 文件如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>io.github.llnancy</groupId>
        <artifactId>xuejian-parent</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>xuejian-apollo</artifactId>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${springboot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!-- web环境 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- apollo客户端 -->
        <dependency>
            <groupId>com.ctrip.framework.apollo</groupId>
            <artifactId>apollo-client</artifactId>
            <version>1.9.2</version>
        </dependency>
    </dependencies>
</project>
```

# 基本配置

在 `Spring Boot` 项目的配置文件 `application.yml` 中添加以下配置项：

```yml
apollo:
  bootstrap:
    enabled: true # 注入默认 application namespace 的配置
    namespaces: application.yml # 注入其它多个 namespace（非默认 application）的配置
    eagerLoad:
      enabled: true # 将 Apollo 配置加载提到初始化日志系统之前：可以将日志相关的配置也交由 Apollo 进行管理

app:
  id: xuejian-apollo
```

注意还需要在 `VM Options` 中配置环境 `env` 和 `Meta Server` 的地址（`apollo.meta`）：

```text
-Denv=DEV -Dapollo.meta=http://localhost:8080
```

# 添加配置

todo

# 读取配置

可使用 `@ApolloConfig`、`@ApolloJsonValue`、`@Value`、`@ConfigurationProperties` 及 `@ApolloConfigChangeListener` 等注解读取配置。

## 使用 @ApolloConfig 注解

使用 `@ApolloConfig` 注解自动注入对应 `namespace` 的 `Config` 对象，代码示例如下：

```java
package io.github.llnancy.xuejian.apollo.web.controller;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Apollo 客户端读取配置项
 * 需添加 VM Options 启动参数：-Denv=DEV -Dapollo.meta=http://localhost:8080
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/27
 */
@RestController
public class ApolloConfigController {

    @ApolloConfig(value = "application.yml")
    private Config config;

    @GetMapping("/config")
    public String getConfig(String key) {
        return config.getProperty(key, null);
    }
}
```

注入了 `application.yml` 这个 `namespace` 对应的 `Config` 对象，从而可以读取 `key-value` 类型的字符串配置项。

## 使用 @ApolloJsonValue 注解

使用 `@ApolloJsonValue` 注解把配置的 `json` 字符串自动注入为 `Java Bean` 对象，代码示例如下：

```java
package io.github.llnancy.xuejian.apollo.web.controller;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloJsonValue;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Apollo 客户端读取配置项
 * 需添加 VM Options 启动参数：-Denv=DEV -Dapollo.meta=http://localhost:8080
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/27
 */
@RestController
public class ApolloConfigController {

    /**
     * yml format:
     * xuejian:
     *   apollo:
     *     jsonstr: '[{"name":"sun", "age": 18}, {"name":"chaser", "age": 20}]'
     */
    @ApolloJsonValue("${xuejian.apollo.jsonstr:[]}")
    private List<JsonBean> jsonBeanList;

    @Data
    public static class JsonBean {
        private String name;
        private Integer age;
    }

    @GetMapping("/json-value")
    public List<JsonBean> getJsonValue() {
        return jsonBeanList;
    }
}
```

> 注意：`yml` 格式下配置 `json` 字符串时需要用单引号 `''` 将整个 `json` 串括起来进行转义。

## 使用 @Value 注解

使用 `@Value` 注释实际上是通过 `Spring` 的 `Placeholder` 占位符的形式来注入配置项，其格式支持 `SpEL` 表达式，例如 `${key:defaultValue}`，冒号左边的 `key` 是配置项，冒号右边的 `defaultValue` 是默认值（可不提供）。代码示例如下：

```java
package io.github.llnancy.xuejian.apollo.web.controller;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloJsonValue;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Apollo 客户端读取配置项
 * 需添加 VM Options 启动参数：-Denv=DEV -Dapollo.meta=http://localhost:8080
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/27
 */
@RestController
@Slf4j
public class ApolloConfigController {

    @Value("${xuejian.apollo.name:defaultVal}")
    private String key;
    
    @Value("#{'${xuejian.apollo.jsonstr:[]}'.split(',')}")
    private List<String> jsonList;

    @GetMapping("/placeholder")
    public void getConfigByPlaceholder() {
        log.info("key={}, jsonList={}", key, jsonList);
    }
}
```

## 配合 @ConfigurationProperties 注解使用

首先定义一个 `config` 配置类代码如下：

```java
package io.github.llnancy.xuejian.apollo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
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
```

然后就可以通过注入的方式进行使用：

```java
package io.github.llnancy.xuejian.apollo.web.controller;

import config.io.github.llnancy.xuejian.apollo.XueJianApolloConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Apollo 客户端读取配置项
 * 需添加 VM Options 启动参数：-Denv=DEV -Dapollo.meta=http://localhost:8080
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/27
 */
@RestController
@Slf4j
public class ApolloConfigController {

    @Autowired
    private XueJianApolloConfig xueJianApolloConfig;

    @GetMapping("/configurationProperties")
    public void getXueJianApolloConfig() {
        String name = xueJianApolloConfig.getName();
        List<String> jsonstr = xueJianApolloConfig.getJsonstr();
        log.info("name={}, jsonstr={}", name, jsonstr);
    }
}
```

## 使用 @ApolloConfigChangeListener 注解

使用 `@ApolloConfigChangeListener` 注解可自动注册 `ConfigChangeListener` 监听器监听配置项变更事件，示例代码如下：

```java
package io.github.llnancy.xuejian.apollo.config;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * apollo 配置更新监听器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/28
 */
@Configuration
@Slf4j
public class ApolloRefresherConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @ApolloConfigChangeListener(value = {"application", "application.yml"})
    private void refreshConfig(ConfigChangeEvent configChangeEvent) {
        log.info("listened config change event, the namespace is {}", configChangeEvent.getNamespace());
        Set<String> changedKeys = configChangeEvent.changedKeys();
        for (String changedKey : changedKeys) {
            ConfigChange change = configChangeEvent.getChange(changedKey);
            log.info("Found change - key: {}, oldValue: {}, newValue: {}, changeType: {}", change.getPropertyName(), change.getOldValue(), change.getNewValue(), change.getChangeType());
        }
        // 更新 @ConfigurationProperties 注解注入的属性
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changedKeys));
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
```

对于使用 `@ConfigurationProperties` 注解注入的属性，需要发布 `org.springframework.cloud.context.environment.EnvironmentChangeEvent` 事件触发属性值的更新。所以需要在 `pom.xml` 中添加 `spring-cloud-context` 依赖：

```xml
<!-- spring cloud 上下文管理   -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-context</artifactId>
    <version>3.1.1</version>
</dependency>
```
