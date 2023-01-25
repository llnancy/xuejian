* [Spring Boot 整合 JSON 序列化](#spring-boot-整合-json-序列化)
* [创建工程](#创建工程)
* [编写 Controller](#编写-controller)
* [使用 jackson](#使用-jackson)
* [使用 gson](#使用-gson)
* [使用 fastjson](#使用-fastjson)
* [总结](#总结)

# Spring Boot 整合 JSON 序列化

在使用 `Spring Boot` 开发 `web` 应用时，我们只需给控制器添加 `@RestController` 注解，接口就能够返回 `json` 格式的数据。实际上该注解是一个组合注解，真正起作用的是 `@ResponseBody`，而它的底层又是由 `HttpMessageConverter` 进行支撑。

`HttpMessageConverter` 是 `spring web` 模块提供的用于转换 `HTTP` 请求和响应的策略接口。它定义了读和写的方法，可以针对不同的数据格式提供不同的读写实现。这里我们讨论对 `application/json` 数据格式的读写，`spring web` 默认集成了 `jackson` 和 `gson` 两个开源 `json` 库，分别提供了 `MappingJackson2HttpMessageConverter` 和 `GsonHttpMessageConverter` 实现类。在 `Spring Boot` 中，默认使用的是 `MappingJackson2HttpMessageConverter`。当然我们也可以替换成 `GsonHttpMessageConverter`，或者集成另外的 `json` 库，例如国内阿里开源的 `fastjson`。

下面分别介绍 `jackson`、`gson` 和 `fastjson` 的使用及对应配置。

# 创建工程

创建 `Spring Boot` 项目 `xuejian-json`，引入相关依赖，完整 `pom.xml` 文件如下：

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

    <artifactId>xuejian-json</artifactId>
    <description>Spring Boot 整合 JSON 序列化</description>

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

# 编写 Controller

创建 `Person.java` 实体类：

```java
package io.github.llnancy.xuejian.json.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 实体类Person
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    private String name;
    
    private Integer age;
    
    private Date birthday;
    
    private LocalDateTime createTime;
}
```

注意，`Person` 类中包含 `Date` 类型的 `birthday` 和 `LocalDateTime` 类型的 `createTime`。

创建 `JsonController.java` 控制器：

```java
package io.github.llnancy.xuejian.json.web.controller;

import io.github.llnancy.xuejian.json.entity.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * json controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/16
 */
@RestController
public class JsonController {

    @GetMapping("/invoke")
    public List<Person> invoke() {
        List<Person> list = new ArrayList<>();
        Person p1 = new Person("p1", 18, new Date(), LocalDateTime.now());
        Person p2 = new Person("p2", 19, new Date(), LocalDateTime.now());
        Person p3 = new Person("p3", 20, new Date(), LocalDateTime.now());
        list.add(p1);
        list.add(p2);
        list.add(p3);
        return list;
    }
}
```

# 使用 jackson

`Spring Boot` 在默认不进行任何配置的情况下使用的就是 `jackson`，启动项目后可直接在浏览器访问接口 `http://localhost:8080/invoke`，可看到输出如下：

```json
[
    {
        "name":"p1",
        "age":18,
        "birthday":"2022-03-17T11:30:47.456+00:00",
        "createTime":"2022-03-17T19:30:47.456"
    },
    {
        "name":"p2",
        "age":19,
        "birthday":"2022-03-17T11:30:47.456+00:00",
        "createTime":"2022-03-17T19:30:47.456"
    },
    {
        "name":"p3",
        "age":20,
        "birthday":"2022-03-17T11:30:47.456+00:00",
        "createTime":"2022-03-17T19:30:47.456"
    }
]
```

注意到 `Date` 和 `LocalDateTime` 这两个时间类型的字段，格式中间带了字母 `T`，不太适合与前端交互且不符合用户常规阅读习惯，一般我们都会对其进行格式转换。

下面我们进行全局配置，创建 `io.github.llnancy.xuejian.json.config.JacksonConfig.java` 配置类：

```java
package io.github.llnancy.xuejian.json.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * {@link Jackson2ObjectMapperBuilderCustomizer} 配置
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/17
 */
@Configuration
public class JacksonConfig {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * {@link JacksonAutoConfiguration} JacksonObjectMapperBuilderConfiguration#jacksonObjectMapperBuilder
     * 通过 {@link Jackson2ObjectMapperBuilderCustomizer} 类来对 {@link com.fasterxml.jackson.databind.ObjectMapper} 对象进行自定义
     * 通过以下方法向 IOC 容器中注入 ObjectMapper 对象：
     * org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.JacksonObjectMapperConfiguration#jacksonObjectMapper(org.springframework.http.converter.json.Jackson2ObjectMapperBuilder)
     * 通过以下方法将 IOC 容器中的 ObjectMapper 对象设置到新建的 {@link MappingJackson2HttpMessageConverter} 对象中并将其注入到 IOC 容器：
     * org.springframework.boot.autoconfigure.http.JacksonHttpMessageConvertersConfiguration.MappingJackson2HttpMessageConverterConfiguration#mappingJackson2HttpMessageConverter(com.fasterxml.jackson.databind.ObjectMapper)
     *
     * @return {@link Jackson2ObjectMapperBuilderCustomizer}
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER))
                .serializerByType(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER))
                .serializerByType(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER))
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER))
                .deserializerByType(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER))
                .deserializerByType(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER))
                // 可用配置项 spring.jackson.date-format=yyyy-MM-dd HH:mm:ss 代替此处的 dateFormat 方法
                .dateFormat(new SimpleDateFormat(DATE_TIME_PATTERN));
    }
}
```

重启项目后浏览器中访问 `http://localhost:8080/invoke`，可看到时间字段已格式化输出：

```json
[
    {
        "name":"p1",
        "age":18,
        "birthday":"2022-03-17 19:47:29",
        "createTime":"2022-03-17 19:47:29"
    },
    {
        "name":"p2",
        "age":19,
        "birthday":"2022-03-17 19:47:29",
        "createTime":"2022-03-17 19:47:29"
    },
    {
        "name":"p3",
        "age":20,
        "birthday":"2022-03-17 19:47:29",
        "createTime":"2022-03-17 19:47:29"
    }
]
```

以上是针对全局的配置，如果我们想针对单独的某个字段进行不一样的格式化输出，可单独将 `com.fasterxml.jackson.annotation.JsonFormat` 注解添加到对应字段上，例如我们要格式化 `birthday` 字段为年月日 `yyyy-MM-dd` 格式：

```java
@JsonFormat(pattern = "yyyy-MM-dd")
private Date birthday;
```

# 使用 gson

排除 `jackson` 依赖后引入 `gson`：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <!-- 排除 jackson -->
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-json</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <!-- 引入 gson -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
    </dependency>
</dependencies>
```

注意排除掉 `jackson` 依赖后上文中创建的 `WebMvcConfig` 类和 `@JsonFormat` 注解都需要进行注释，重启项目后浏览器访问 `http://localhost:8080/invoke`，可看到输出如下：

```json
[
    {
        "name":"p1",
        "age":18,
        "birthday":"Mar 17, 2022 8:08:08 PM",
        "createTime":{
            "date":{
                "year":2022,
                "month":3,
                "day":17
            },
            "time":{
                "hour":20,
                "minute":8,
                "second":8,
                "nano":32000000
            }
        }
    },
    {
        "name":"p2",
        "age":19,
        "birthday":"Mar 17, 2022 8:08:08 PM",
        "createTime":{
            "date":{
                "year":2022,
                "month":3,
                "day":17
            },
            "time":{
                "hour":20,
                "minute":8,
                "second":8,
                "nano":32000000
            }
        }
    },
    {
        "name":"p3",
        "age":20,
        "birthday":"Mar 17, 2022 8:08:08 PM",
        "createTime":{
            "date":{
                "year":2022,
                "month":3,
                "day":17
            },
            "time":{
                "hour":20,
                "minute":8,
                "second":8,
                "nano":32000000
            }
        }
    }
]
```

可以看到两个时间字段格式仍然展示不友好。下面我们进行全局的配置，创建 `io.github.llnancy.xuejian.json.config.GsonConfig` 配置类：

```java
package io.github.llnancy.xuejian.json.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * {@link GsonBuilderCustomizer} 配置
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/17
 */
@Configuration
public class GsonConfig {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * {@link GsonAutoConfiguration#gsonBuilder(List)} 通过 {@link GsonBuilderCustomizer} 类来对 {@link com.google.gson.Gson} 对象进行自定义
     * {@link GsonAutoConfiguration#gson(GsonBuilder)} 向 IOC 容器中注入 Gson 对象
     * 通过以下方法将 IOC 容器中的 Gson 对象设置到新建的 {@link GsonHttpMessageConverter} 对象中并将其注入到 IOC 容器
     * org.springframework.boot.autoconfigure.http.GsonHttpMessageConvertersConfiguration.GsonHttpMessageConverterConfiguration#gsonHttpMessageConverter(com.google.gson.Gson)
     *
     * @return {@link GsonBuilderCustomizer}
     */
    @Bean
    public GsonBuilderCustomizer gsonBuilderCustomizer() {
        return gsonBuilder -> gsonBuilder.registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DATE_TIME_FORMATTER)))
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DATE_FORMATTER)))
                .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(TIME_FORMATTER)))
                // 可用配置项 spring.gson.date-format=yyyy-MM-dd HH:mm:ss 代替此处的 setDateFormat 方法
                .setDateFormat(DATE_TIME_PATTERN);
    }
}
```

重启项目后访问 `http://localhost:8080/invoke` 即可看到格式化的时间字段。

# 使用 fastjson

> `fastjson` 是阿里巴巴开源的 `JSON` 库。在阿里有着大规模的生产实践，但在 `2020` 年左右安全漏洞问题频发，需要不停升级版本进行修复，不推荐使用。

同样地，排除 `jackson` 依赖后引入 `fastjson`：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <!-- 排除 jackson -->
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-json</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <!-- fastjson -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>1.2.79</version>
    </dependency>
</dependencies>
```

`spring web` 模块没有提供基于 `fastjson` 的 `HttpMessageConverter` 实现类，我们需要显式配置 `fastjson` 包提供的 `FastJsonHttpMessageConverter`。创建 `AlibabaFastJsonConfig.java` 配置类编写代码如下：

```java
package io.github.llnancy.xuejian.json.config;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link FastJsonHttpMessageConverter} 配置
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/18
 */
@Configuration
public class AlibabaFastJsonConfig {

    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = converter.getFastJsonConfig();
        // 格式化时间类型字段 Date 和 LocalDateTime
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        // 添加支持的 MediaType 类型。由于 FastJsonHttpMessageConverter 的无参构造器中设置的 MediaType 类型为 ALL
        // 会导致 org.springframework.http.HttpHeaders.setContentType 方法抛出 IllegalArgumentException 异常
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED));
        return converter;
    }
}
```

重启项目后访问 `http://localhost:8080/invoke` 即可看到格式化输出的 `json` 串。

# 总结

本文主要介绍了 `Spring Boot` 集成配置三个 `json` 库的方式，完整代码可查看 [`Github`](https://github.com/llnancy/xuejian/tree/master/xuejian-json)。
