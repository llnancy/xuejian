# `SpringBoot`整合`JSON`序列化

在使用`Spring Boot`开发`web`应用时，我们只需给控制器添加`@RestController`注解，接口就能够返回`json`格式的数据。实际上该注解是一个组合注解，真正起作用的是`@ResponseBody`，而它的底层又是由`HttpMessageConverter`进行支撑。

`HttpMessageConverter`是`spring web`模块提供的用于转换`HTTP`请求和响应的策略接口。它定义了读和写的方法，可以针对不同的数据格式提供不同的读写实现。这里我们讨论对`application/json`格式的读写，`spring web`默认集成了`jackson`和`gson`这两个开源`json`库，提供了`MappingJackson2HttpMessageConverter`和`GsonHttpMessageConverter`这两个实现。在`Spring Boot`中，默认使用的是`MappingJackson2HttpMessageConverter`。当然我们也可以替换成`GsonHttpMessageConverter`，或者集成一些其它的`json`库，例如国内阿里开源的`fastjson`。

下面分别介绍一下如何使用`jackson`、`gson`和`fastjson`以及对应的最优配置。

## 创建工程

创建`Spring Boot`项目`oxygen-json`，引入相关依赖，完整`pom.xml`文件如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>sunchaser-oxygen</artifactId>
        <groupId>com.sunchaser.oxygen</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>oxygen-json</artifactId>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <springboot.version>2.6.4</springboot.version>
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

## 编写`Controller`

创建`com.sunchaser.oxygen.json.entity.Person.java`实体类：

```java
package com.sunchaser.oxygen.json.entity;

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

注意，`Person`类中包含`Date`类型的`birthday`和`LocalDateTime`类型的`createTime`。

创建`com.sunchaser.oxygen.json.web.controller.JsonController.java`控制器：

```java
package com.sunchaser.oxygen.json.web.controller;

import com.sunchaser.oxygen.json.entity.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
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

## 使用`jackson`

`Spring Boot`在默认不进行任何配置的情况下使用的就是`jackson`，启动项目后可直接在浏览器访问接口`http://localhost:8080/invoke`，可看到输出如下：

```
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

注意到`Date`和`LocalDateTime`这两个时间类型的字段，格式中间带了字母`T`，不太适合与前端交互且不符合用户常规阅读习惯，一般我们都会对其进行格式转换。

这里我们进行全局的配置，创建`com.sunchaser.oxygen.json.config.JacksonConfig.java`配置类：

```java
package com.sunchaser.oxygen.json.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * MappingJackson2HttpMessageConverter 配置
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/17
 */
@Configuration
public class JacksonConfig {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    // @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        // 格式化Date
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // 时间模块：格式化Java8的LocalDateTime
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));
        objectMapper.registerModule(javaTimeModule);

        converter.setObjectMapper(objectMapper);
        return converter;
    }
}
```

重启项目后浏览器中访问`http://localhost:8080/invoke`，可看到时间字段已格式化输出：

```
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

以上是针对全局的配置，如果我们想针对单独的某个字段进行不一样的格式化输出，可单独将`com.fasterxml.jackson.annotation.JsonFormat`注解添加到对应字段上，例如我们要格式化`birthday`字段为`yyyy-MM-dd`年月日格式：

```
@JsonFormat(pattern = "yyyy-MM-dd")
private Date birthday;
```

## 使用`gson`

`Spring Boot`是基于条件注解和自动装配的，我们只需要引入`gson`的依赖并排除`jackson`即可。修改`pom.xml`文件如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>sunchaser-oxygen</artifactId>
        <groupId>com.sunchaser.oxygen</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>oxygen-json</artifactId>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <springboot.version>2.6.4</springboot.version>
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
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <!-- 排除jackson -->
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-json</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!-- 引入gson -->
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
        </dependency>
    </dependencies>
</project>
```

注意排除掉`jackson`依赖后上文中创建的`WebMvcConfig`类和`@JsonFormat`注解都需要进行注释，重启项目后浏览器访问`http://localhost:8080/invoke`，可看到输出如下：

```
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

可以看到两个时间字段格式仍然展示不友好。下面我们进行全局的配置，创建`com.sunchaser.oxygen.json.config.GsonConfig`配置类：

```java
package com.sunchaser.oxygen.json.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * GsonHttpMessageConverter 配置
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/17
 */
@Configuration
public class GsonConfig {

    @Bean
    public GsonHttpMessageConverter gsonHttpMessageConverter() {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        GsonBuilder builder = new GsonBuilder();
        // 格式化Date
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        // 格式化LocalDateTime
        builder.registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> {
            String format = src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return new JsonPrimitive(format);
        });
        builder.registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> {
            String format = src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return new JsonPrimitive(format);
        });
        builder.registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) -> {
            String format = src.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            return new JsonPrimitive(format);
        });
        Gson gson = builder.create();
        converter.setGson(gson);
        return converter;
    }
}
```

重启项目后访问`http://localhost:8080/invoke`即可看到格式化的时间字段。

## 使用`fastjson`

> `fastjson`是阿里巴巴开源的`JSON`库。在阿里有着大规模的生产实践，但`2020`年左右安全漏洞问题频发，导致需要不停升级版本修复漏洞，不是很推荐使用。

同样地，引入`fastjson`的依赖并排除默认`jackson`依赖。修改后的`pom.xml`文件如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>sunchaser-oxygen</artifactId>
        <groupId>com.sunchaser.oxygen</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>oxygen-json</artifactId>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <springboot.version>2.6.4</springboot.version>
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
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <!-- 排除jackson -->
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-json</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!-- fastjson -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.79</version>
        </dependency>
    </dependencies>
</project>
```

默认情况下`spring web`模块中没有提供`fastjson`的`HttpMessageConverter`，需要我们显式配置`fastjson`包中的`FastJsonHttpMessageConverter`，同时格式化时间类型字段。创建`com.sunchaser.oxygen.json.config.AlibabaFastJsonConfig.java`配置类编写代码如下：

```java
package com.sunchaser.oxygen.json.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * FastJsonHttpMessageConverter 配置
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/18
 */
@Configuration
public class AlibabaFastJsonConfig {

    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 格式化时间类型字段Date和LocalDateTime
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        converter.setFastJsonConfig(fastJsonConfig);
        return converter;
    }
}
```

重启项目后访问`http://localhost:8080/invoke`即可看到格式化输出的`json`串。

## 总结

本文主要介绍了`Spring Boot`集成配置三个`json`库的方式，完整代码可查看 [`Github`](https://github.com/sunchaser-lilu/sunchaser-oxygen/tree/master/oxygen-json)。
