* [第一个Spring Boot项目](#第一个spring-boot项目)
* [在线网站创建](#在线网站创建)
    * [基于Spring Initializr](#基于spring-initializr)
    * [基于Maven创建](#基于maven创建)
    * [基于Gradle](#基于gradle)
* [编写启动类](#编写启动类)
* [编写Controller](#编写controller)
* [启动Spring Boot](#启动spring-boot)

# 第一个`Spring Boot`项目

可通过以下方式创建`Spring Boot`项目：

1. 在线网站：导出压缩包后导入`IDEA`。
    - `Spring`：[`https://start.spring.io`](https://start.spring.io)
    - `Aliyun`：[`https://start.aliyun.com`](https://start.aliyun.com)
2. `IDEA`：集成在线网站或从零构建
    - 基于`Spring Initializr`
    - 基于`Maven`构建
    - 基于`Gradle`构建

# 在线网站创建

`Spring`官方：[`https://start.spring.io`](https://start.spring.io)

![start.spring.io](https://posts-cdn.lilu.org.cn/2022/05/1920a9be1340a33f4518a40910da6702b6e88fc2.png)

`Aliyun`：[`https://start.aliyun.com`](https://start.aliyun.com)

![start.aliyun.com](https://posts-cdn.lilu.org.cn/2022/05/1920a96fe7c583bda3cdfc78a4bc96b39fc3faae.png)

填写相关配置，选择所需依赖，点击下载即可。

# `IDEA`构建

## 基于`Spring Initializr`

`Spring Initializr`：

![spring-initializr](https://posts-cdn.lilu.org.cn/2022/05/192023a6cf9802d71d4ef0417806652390e6c5f6.png)

修改`Server URL`即可修改在线地址。点击`Next`可进入下一步：

`start.spring.io`：

![spring-initializr-next](https://posts-cdn.lilu.org.cn/2022/05/19203f19e691eb03e7f02bc47d4794f7416f8900.png)

`start.aliyun.com`：

![aliyun-initializr-next](https://posts-cdn.lilu.org.cn/2022/05/192073dd84dabc7c58d81ce128615abce82cf222.png)

选择所需依赖后点击`Finish`即可。

## 基于`Maven`创建

基于`Maven`从零构建`Spring Boot`项目。

创建`Maven`空模板项目：

![maven-module](https://posts-cdn.lilu.org.cn/2022/05/1920ab4092d827ea5343cd3fff07ce202cff03f1.png)

点击`Next`填写相关信息：

![maven-module-next](https://posts-cdn.lilu.org.cn/2022/05/1920b6540519d12c0b6fa46540a3207ca30f3ac6.png)

点击`Finish`完成。创建好的项目工程结构如下：

![maven-module-dir-struct](https://posts-cdn.lilu.org.cn/2022/05/19207c50ed38b71b0ec9bc7dfaae9f9a2b54b7c2.png)

可以看到是非常干净的`Maven`项目结构。打开`pom.xml`文件添加`Spring Boot`的依赖，这里有两种方式：

方式一：使用`parent`标签继承`spring-boot-starter-parent`。

完整`pom`如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.6.4</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>chunyu-helloworld</artifactId>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
</project>
```

方式二：使用预定义依赖标签`dependencyManagement`引入`spring-boot-dependencies`。

完整`pom`如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>com.sunchaser.chunyu</groupId>
        <artifactId>sunchaser-chunyu</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>chunyu-helloworld</artifactId>

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
    </dependencies>
</project>
```

使用预定义依赖后，在`dependencies`标签中引入依赖无需指定版本号，较为优雅。

## 基于`Gradle`

在线网站或`Spring Initializr`方式均可选择构建系统为`Gradle`。

# 编写启动类

创建`Spring Boot`启动类`com.sunchaser.chunyu.helloworld.ChunYuHelloWorldApplication.java`，编写代码如下：

```java
package com.sunchaser.chunyu.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * hello world 项目启动类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/6
 */
@SpringBootApplication
public class ChunYuHelloWorldApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ChunYuHelloWorldApplication.class, args);
    }
}
```

`main`方法中还可使用建造者模式启动`Spring Boot`，代码如下：

```java
package com.sunchaser.chunyu.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * hello world 项目启动类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/6
 */
@SpringBootApplication
public class ChunYuHelloWorldApplication {
    
    public static void main(String[] args) {
        // SpringApplication.run(ChunYuHelloWorldApplication.class, args);
        new SpringApplicationBuilder(ChunYuHelloWorldApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
```

# 编写`Controller`

创建`com.sunchaser.chunyu.helloworld.controller.HelloController.java`类，编写代码如下：

```java
package com.sunchaser.chunyu.helloworld.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello Controller
 * 
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/6
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring Boot";
    }
}
```

# 启动`Spring Boot`

运行`ChunYuHelloWorldApplication#main`方法，访问[`http://localhost:8080/hello`](http://localhost:8080/hello)。

完整代码可查看 [`Github`](https://github.com/sunchaser-lilu/sunchaser-chunyu/tree/master/chunyu-hello-world)。
