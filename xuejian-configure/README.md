- [Spring Boot 配置属性](#spring-boot-配置属性)
- [properties 还是 yml？](#properties-还是-yml)
- [创建工程](#创建工程)
- [单一属性注入](#单一属性注入)
- [多级属性注入](#多级属性注入)
- [数组类型](#数组类型)
- [变量引用](#变量引用)
- [处理转义字符](#处理转义字符)
- [@Value 设置默认值](#value-设置默认值)
  - [获取全部配置项](#获取全部配置项)
- [面向对象式封装](#面向对象式封装)
- [临时属性](#临时属性)
  - [原理](#原理)
- [多环境配置](#多环境配置)
- [总结](#总结)

# Spring Boot 配置属性

`Spring Boot` 的核心理念是约定优于配置，其中就包括约定默认配置文件是 `application.properties` 或 `application.yml`。对于应用层的配置，我们一般不需要进行修改；大多情况下都是业务层需要进行自定义的业务属性配置，例如下游接口 `URL`、自定义属性、默认值等。本文主要介绍在 `Spring Boot` 的配置文件中如何注入各种数据类型的属性以及如何进行属性值的覆盖。

# properties 还是 yml？

如果是使用在线网站或 `Spring Initializr` 创建的 `Spring Boot` 项目，则默认生成的配置文件是 `application.properties`。我们也可以手动替换成 `application.yml` 或 `application.yaml` 文件，`.yml` 和 `.yaml` 结尾的都是 `yaml` 格式，没有本质上的区别，`spring-boot-starter-parent` 中对这三种后缀的文件按顺序进行了引入：

```xml
    <resources>
      <resource>
        <directory>${basedir}/src/main/resources</directory>
        <filtering>true</filtering>
        <includes>
          <include>**/application*.yml</include>
          <include>**/application*.yaml</include>
          <include>**/application*.properties</include>
        </includes>
      </resource>
      <resource>
        <directory>${basedir}/src/main/resources</directory>
        <excludes>
          <exclude>**/application*.yml</exclude>
          <exclude>**/application*.yaml</exclude>
          <exclude>**/application*.properties</exclude>
        </excludes>
      </resource>
    </resources>
```

所以如果这三个文件共存，对于相同的配置项，`application.properties` 文件中的配置优先级最高（后者覆盖前者）。

> `yaml` 语法：
>
> - `#` 表示注释
> - 大小写敏感。
> - 对象属性层级关系使用多行描述，每行结尾用英文冒号`:`结束。
> - 使用缩进表示层级关系，同层级左侧进行对齐，只允许使用空格（不允许使用 `Tab` 键）。
> - 属性值前面需加一个空格（英文冒号`:`和属性值之间）。

对于 `properties` 和 `yml` 的选择，可根据个人喜好进行选择，个人推荐选用 `yml` 格式，会更加面向对象和结构化。当然这两种格式也可相互进行转化。下文会以 `yml` 文件为例介绍各种数据类型的属性注入。

# 创建工程

创建 `Spring Boot` 项目 `xuejian-configure`，引入相关依赖，完整 `pom.xml` 文件如下：

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

    <artifactId>xuejian-configure</artifactId>
    <description>Spring Boot 属性配置</description>

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
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>
</project>
```

# 单一属性注入

在 `resources` 目录下新建 `application.yml` 文件。

单一属性就是普通的 `kv` 键值对，例如：

```yml
title: Spring Boot!
```

在 `Spring Boot` 中可使用 `@Value` 注解获取属性值，支持 `SpEL` 表达式。代码示例如下：

```java
package io.github.llnancy.xuejian.configure.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @GetMapping("/yml")
    public void yml() {
        // 单一属性注入
        log.info("title={}", title);
    }
}
```

# 多级属性注入

多级属性体现了面向对象的思想，例如：

```yml
# 多级属性
xuejian:
  user:
    name: 胡歌
    age: 18
```

有两级，同样使用 `@Value` 注解进行获取，代码示例如下：

```java
package io.github.llnancy.xuejian.configure.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${xuejian.user.name}")
    private String name;

    @Value("${xuejian.user.age}")
    private Integer age;

    @GetMapping("/yml")
    public void yml() {
        // 多级属性注入
        log.info("xuejian.user.name={}", name);
        log.info("xuejian.user.age={}", age);
    }
}
```

# 数组类型

`yml` 中的数组类型有两种写法，示例如下：

```yml
languages:
  - Java
  - Python
  - Go

platforms: [pc, android, ios, mini]
```

使用 `@Value` 注解获取时索引从 `0` 开始，代码示例如下：

```java
package io.github.llnancy.xuejian.configure.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${languages[0]}")
    private String language;

    @Value("${platforms[1]}")
    private String platform;

    @GetMapping("/yml")
    public void yml() {
        // 数组类型注入
        log.info("language[0]={}", language);
        log.info("platform[1]={}", platform);
    }
}
```

# 变量引用

在业务开发中我们可能需要配置下游接口的多个 `URL`，按照上文介绍的属性注入方式写法可能是下面这样的：

```yml
user-service:
  urls:
    get-users: https://domain.com/getUsers
    add-user: https://domain.com/addUser
    update-user: https://domain.com/updateUser
```

这时如果下游接口域名发生变化，我们就需要同时更改三个配置项，比较繁琐。`Spring Boot` 中允许我们使用变量引用的形式提取出一些公共配置项，写法是 `${}` 的形式，例如我们可以将域名提取成 `base-domain`，在具体的 `url` 中使用变量引用的形式配置。下面是写法示例：

```yml
user-service:
  base-domain: https://domain.com
  urls:
    get-users: ${user-service.base-domain}/getUsers
    add-user: ${user-service.base-domain}/addUser
    update-user: ${user-service.base-domain}/updateUser
```

# 处理转义字符

有时候会遇到配置项的值中包含转义字符，例如：

```yml
escape-character: \temp
```

`\t` 是水平制表转义字符，上述写法直接使用 `@Value(${escape-character})` 得到的值是 `\temp` 字符串。如果我们希望读取到转义字符而不是`\t`本身，可以对整个字符串加上双引号进行处理：

```yml
escape-character: "\temp"
```

这时得到的值是 ` emp`。

# @Value 设置默认值

在使用 `@Value` 注解时，如果 `.yml` 配置文件中没有对配置项进行配置，则 `Spring Boot` 启动会报错。这时我们可以用冒号 `:` 语法在 `@Value` 注解的 `value` 中给配置项设置一个默认值。代码示例如下：

```java
@Value("${default-value:abc}")
private String defaultV;
```

## 获取全部配置项

当配置项较多时，每个配置项都使用 `@Value` 注解来获取会显得比较累赘，这时我们可以通过 `org.springframework.core.env.Environment` 类获取全部的配置信息。代码示例如下：

```java
package io.github.llnancy.xuejian.configure.web.controller;

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

    @Autowired
    private Environment env;

    @GetMapping("/yml")
    public void yml() {
        // Environment
        log.info("Environment#xuejian.user.name={}", env.getProperty("xuejian.user.name"));
        log.info("Environment#languages[0]={}", env.getProperty("languages[0]"));
        log.info("user-service.urls.get-users={}", env.getProperty("user-service.urls.get-users"));
        log.info("default-value={}", env.getProperty("default-value", "env-abc"));
    }
}
```

# 面向对象式封装

虽然使用 `Environment` 能获取到全部的配置项，但获取值时仍需要根据单个 `key` 进行获取，没有将这些 `key` 封装到不同的 `JavaBean` 中。

下面以下游接口 `urls` 配置项为例进行面向对象式封装。代码示例如下：

```java
package io.github.llnancy.xuejian.configure.config;

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
```

提供配置项对应的 `Java` 实体类并交给 `Spring` 进行管理，然后使用 `@ConfigurationProperties(prefix = "user-service")` 注解声明这是一个属性配置类，并指定配置项的公共前缀是 `user-service`。`Spring` 容器在启动过程中会将 `.yml` 文件中配置项的值 `set` 到容器中对应 `bean` 的属性上（动态绑定），于是我们可以直接进行依赖注入使用：

```java
package io.github.llnancy.xuejian.configure.web.controller;

import io.github.llnancy.xuejian.configure.config.UserServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserServiceConfig userServiceConfig;

    @GetMapping("/yml")
    public void yml() {
        // @ConfigurationProperties
        log.info("userServiceConfig={}", userServiceConfig);
    }
}
```

以上就是对配置项的面向对象式封装。细心的同学可能会发现，`IDEA` 中，`UserServiceConfig` 类文件上方出现了一个暗红色背景的横幅提示：`Spring Boot Configuration Annotation Processor not configured`（`Spring Boot` 注解执行器没有配置）。

![annotation-processor-not-configured](https://cdn.jsdelivr.net/gh/llnancy/sunchaser-cdn@master/images/java-ee/springboot/configure/annotation-processor-not-configured.png)

这是什么意思呢？举个简单的例子，我们在 `.yml` 文件中添加 `server.port` 配置项修改端口号，将鼠标移到 `port` 位置上，按住 `ctrl(command)` 并点击鼠标左键，会发现 `IDEA` 跳转到了 `org.springframework.boot.autoconfigure.web.ServerProperties.class` 文件，而同样的方式点击 `user-service` 配置项下面的配置会发现没有任何跳转动作。另外我们会发现输入 `server` 后会弹出相应提示，而我们输入 `user-service` 后确没有任何的提示。

解决办法是引入注解执行器的依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

引入依赖后需要重新 `build` 一下工程，或者 `mvn clean` 清理 `target` 目录后重新 `mvn compile` 进行编译。这样我们就可以从配置类跳转到 `.yml` 文件中对应的配置项位置。

# 临时属性

对于 `application.yml` 文件中的配置项，如果项目已经打成 `jar` 包了，在无法修改源码重新打包的情况下如果想替换某些配置项的值，可以在 `java -jar` 命令启动 `jar` 包时添加临时属性，它的优先级大于 `application.yml` 配置文件。例如修改 `server.port` 端口号，启动命令如下：

```shell
java -jar app.jar --server.port=8081
```

多个临时属性之间用空格分隔，每个临时属性前面都要加两个减号 `--`。

## 原理

临时参数实际上会被启动类的 `main` 方法的 `args` 参数接收：

```java
@SpringBootApplication
public class XueJianConfigureApplication {
    
    public static void main(String[] args) {
        // SpringApplication.run(XueJianConfigureApplication.class, args);
        new SpringApplicationBuilder(XueJianConfigureApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args); // 添加临时参数args
    }
}
```

所以启动类中一定要把 `args` 参数传递给 `SpringApplication`，否则无法生效。

# 多环境配置

实际企业开发中我们会面对多个环境的配置，例如：

- `local`：本地环境。通常所有的配置都在本地，例如连接本地的数据库等。
- `dev`：开发环境。用于开发联调。
- `fat`：功能验收测试环境。提供给测试同学使用，又叫 `test`。
- `uat`：用户验收测试环境。又叫预发布环境 `pre`，此环境下填写的配置和生产环境保持一致。
- `pro`：线上环境。

通常我们会为每一个环境建立一个配置文件，例如本地环境 `local`，对应的配置文件名为 `application-local.yml`，`application` 和环境标识 `local` 之间用 `-` 进行连接。另外我们还会建立 `application.yml` 主配置文件，可以存放一些所有环境公用的配置并设置让哪个环境的配置生效。

例如我们想配置不同的环境使用不同的端口号，各配置文件的写法如下：

`application-local.yml`：

```yml
server:
  port: 8000
```

`application-dev.yml`：

```yml
server:
  port: 8001
```

`application-uat.yml`：

```yml
server:
  port: 8002
```

`application-fat.yml`：

```yml
server:
  port: 8003
```

`application-pro.yml`：

```yml
server:
  port: 8004
```

最后我们需要配置 `application.yml` 文件让某个环境的配置生效，例如本地开发时设置 `local` 生效：

`application.yml`

```yml
spring:
  profiles:
    active: local
```

> 注意如果 `application.yml` 文件中存在相同的配置项，例如配置了：
>
> ```yml
> server:
>   port: 80
> ```
>
> 则该配置并不会生效，会以 `application-local.yml` 中的配置为准。

# 总结

以上就是 `Spring Boot` 中和配置文件有关的内容，主要介绍了各种类型的属性注入方式以及多环境下的配置方式。所有的特性也均适用于 `.properties` 文件。完整代码可查看 [Github](https://github.com/llnancy/xuejian/tree/master/xuejian-configure)。
