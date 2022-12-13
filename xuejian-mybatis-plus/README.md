- [`Spring Boot` 整合 `MyBatis-Plus` 快速实现单表 `CRUD`](#spring-boot-整合-mybatis-plus-快速实现单表-crud)
  - [数据表准备](#数据表准备)
  - [创建工程](#创建工程)
  - [代码生成](#代码生成)
  - [生成代码介绍](#生成代码介绍)
    - [`entity`](#entity)
    - [`mapper`](#mapper)
    - [`service & impl`](#service--impl)
    - [`controller`](#controller)
  - [基础配置](#基础配置)
  - [使用 `BaseMapper`](#使用-basemapper)
  - [使用 `IService`](#使用-iservice)
  - [条件构造器 `Wrapper`](#条件构造器-wrapper)
  - [插件](#插件)
    - [分页插件](#分页插件)
      - [配置类](#配置类)
    - [使用分页](#使用分页)
  - [其它](#其它)

# `Spring Boot` 整合 `MyBatis-Plus` 快速实现单表 `CRUD`

[`MyBatis-Plus`](https://baomidou.com/)（简称 `MP`）是一个 `MyBatis` 的增强工具，在 `MyBatis`
的基础上只做增强不做改变，为简化开发、提高效率而生。本文主要介绍在 `Spring Boot` 中整合 `Mybatis-Plus` 快速实现 `MySQL`
单表 `crud` 的操作。

核心依赖：

```xml
<!-- mybatis plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.2</version>
</dependency>
```

## 数据表准备

在创建工程之前，我们先准备一张用来 `crud` 的数据表。数据库我们选用 `MySQL`，下面是初始化脚本：

```sql
CREATE DATABASE `chunyu_mp` default character set utf8mb4 COLLATE utf8mb4_general_ci;
use `chunyu_mp`;

DROP TABLE IF EXISTS `mp_user`;
CREATE TABLE `mp_user`
(
    `id`          bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `name`        varchar(18)         NOT NULL DEFAULT '无名氏' COMMENT '姓名',
    `age`         tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '年龄',
    `address`     varchar(64)         NOT NULL DEFAULT '' COMMENT '地址',
    `create_time` datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`  tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '状态（0：正常；1：删除）',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='Mybatis-Plus 用户表';
```

## 创建工程

创建 `Spring Boot` 项目 `chunyu-mybatis-plus`，引入相关依赖，完整 `pom.xml` 文件如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>sunchaser-chunyu</artifactId>
        <groupId>com.sunchaser.chunyu</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>chunyu-mybatis-plus</artifactId>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <!-- Spring Boot 版本-->
        <springboot.version>2.6.4</springboot.version>
        <!-- MyBatis-Plus 版本-->
        <mybatisplus.version>3.5.2</mybatisplus.version>
        <!-- 代码生成器版本 -->
        <mybatisplus.generate.version>3.5.2</mybatisplus.generate.version>
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
        <!-- web 环境 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- mybatis plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatisplus.version}</version>
        </dependency>
        <!-- mybatis plus generate 代码生成器 -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-generator</artifactId>
            <version>${mybatisplus.generate.version}</version>
        </dependency>
        <!-- 数据库驱动 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <!-- 模板引擎 freemarker -->
        <dependency>
            <groupId>org.freemarker</groupId>
            <artifactId>freemarker</artifactId>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>
</project>
```

这里为了“快”，我们利用 `mybatis-plus-generate` 代码生成器生成 `entity`、`mapper`、`service`、`impl` 及 `controller`
等文件，选用 `freemarkder` 模板引擎。

> 版本：`MP` 选用当前最新版本 `3.5.2`，代码生成器选择 `3.5.2`。

## 代码生成

创建 `MybatisPlusGenerator.java` 类，编写代码生成的 `main` 方法如下：

```java
package com.sunchaser.chunyu.mybatisplus.generate;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.INTEGER;

/**
 * mybatis plus 3.5.2 版本以上的代码生成器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/9
 */
public class MybatisPlusGenerator {

    public static void main(String[] args) {
        FastAutoGenerator.create(new DataSourceConfig
                        .Builder("jdbc:mysql://localhost:3306/chunyu_mp?useUnicode=true&characterEncoding=UTF-8&useSSL=false", "root", "123456")
                        // 自定义类型转换器：tinyint 生成 Integer 替换默认的 Boolean
                        .typeConvert(new MySqlTypeConvert() {
                            @Override
                            public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                                IColumnType columnType = super.processTypeConvert(globalConfig, fieldType);
                                if (fieldType.contains("tinyint")) {
                                    columnType = INTEGER;
                                }
                                return columnType;
                            }
                        })
                        // 处理数据库关键字
                        .keyWordsHandler(new MySqlKeyWordsHandler())
                )
                .globalConfig(builder -> {
                    builder.author("sunchaser admin@lilu.org.cn") // 设置类文件头部注释的作者
                            .fileOverride() // 覆盖已生成文件（即将过时）3.5.2 版本不会进行覆盖
                            .commentDate(() -> "JDK8 " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))) // 设置类文件头部注释的时间
                            .dateType(DateType.TIME_PACK) // 使用 Java8 的新时间类型 LocalDateTime
                            .outputDir("./chunyu-mybatis-plus/src/main/java"); // 指定输出目录（相对 or 绝对路径均可）
                })
                .packageConfig(builder -> {
                    builder.parent("com.sunchaser.chunyu") // 设置父包名
                            .moduleName("mybatisplus") // 设置父包模块名
                            .entity("repository.entity") // entity 包名
                            .mapper("repository.mapper") // mapper 包名
                            .controller("web.controller") // controller 包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "./chunyu-mybatis-plus/src/main/resources/mapper")); // 指定 xml 文件生成的路径
                })
                .strategyConfig(builder -> {
                    builder.addTablePrefix("mp_") // 增加过滤表前缀
                            .entityBuilder() // Entity 策略配置
                            .enableLombok() // 开启 lombok
                            .formatFileName("%sEntity") // 实体类以 Entity 结尾
                            .logicDeleteColumnName("is_deleted") // 逻辑删除字段
                            // .superClass("com.sunchaser.shushan.wulingzhu.repository.entity.BaseEntity")
                            // .addSuperEntityColumns("id", "create_time", "update_time", "is_deleted")// 设置实体公共父类字段
                            .controllerBuilder() // Controller 策略配置
                            .enableHyphenStyle() // 驼峰转连字符 -
                            .enableRestStyle() // 使用 @RestController
                            .serviceBuilder() // Service 策略配置
                            .formatServiceFileName("%sService");// service 接口以 Service 结尾
                })
                .templateConfig(builder -> {
                    builder.entity("/templates/entity.java") // 配置自定义的 entity 模板位置（不用带 .ftl 模板引擎后缀名），使用 @Data 注解
                            .mapper("/templates/mapper.java") // 自定义 mapper 模板位置，去掉默认的 <p></p> 标签
                            .service("/templates/service.java") // 自定义 service 模板位置，去掉默认的 <p></p> 标签
                            .serviceImpl("/templates/serviceImpl.java") // 自定义 serviceImpl 模板位置，去掉默认的 <p></p> 标签
                            .controller("/templates/controller.java"); // 自定义 controller 模板位置，去掉默认的 <p></p> 标签
                })
                // 使用 Freemarker 引擎模板，默认的是 Velocity 引擎模板
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
```

这里使用到了自定义模板，主要有两点原因，一是因为该版本官方提供的默认 `entity` 模板生成的实体类上使用的是 `@Getter`
和`@Setter` 注解，而不是 `@Data`；另外一点是一些细微的地方不符合个人习惯，例如类注释上默认会带有 `<p></p>` 标签占用两行位置等。

官方的模板文件在生成器 `jar` 包的 `templates` 目录下，我们使用的是 `freemarker` 模板引擎，所以选择 `.ftl`
结尾的文件，这里我以 `entity.java.ftl` 为例，首先拷贝模板文件到工程的 `resources/templates`
目录下，然后将文件中的 `import lombok.Getter;import lombok.Setter;` 替换为 `import lombok.Data;`，`@Getter` 和 `@Setter`
替换为 `@Data`。

> `Tips`：`IDEA` 中默认粘贴多行代码会自动缩进，如果不是直接复制文件，而是全选代码进行复制粘贴的话可能会遇到缩进问题。`IDEA`
> 提供的设置项为：`setting -> Editor -> General -> Smart keys`，`Reformat on paste` 选择 `None` 则会取消自动缩进。

运行 `main` 方法即可生成相关类文件，非常方便快捷好用，强力推荐。

## 生成代码介绍

### `entity`

`UserEntity.java` 类，由于我们自定义了模板，所以类上是 `@Data` 注解。类中还有几个其它注解，这里进行简单说明：

- `@TableName("mp_user")`：标识实体类对应的表名。
- `@TableId(value = "id", type = IdType.AUTO)`：标识主键字段，`value` 表示主键字段名，`type`
  表示主键类型，这里 `IdType.AUTO` 表示数据库自增。
- ``@TableField("`name`")``：标识数据库字段名，这里主要是因为 `name` 是 `MySQL` 中的关键字，所以要加反引号 `` ` ``。
- `@TableLogic`：标识数据库表的逻辑删除字段。加上该注解后所有的查询修改删除都会自动带上该字段作为条件。

### `mapper`

`UserMapper.java` 类：

```java
package com.sunchaser.chunyu.mybatisplus.repository.mapper;

import entity.repository.io.github.llnancy.xuejian.mybatisplus.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * Mybatis-Plus 用户表 Mapper 接口
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/11/01
 */
public interface UserMapper extends BaseMapper<UserEntity> {

}
```

继承 `com.baomidou.mybatisplus.core.mapper.BaseMapper` 类拥有常用 `crud` 方法：

| 方法签名                                                                                        | 描述                                                                   |
|---------------------------------------------------------------------------------------------|----------------------------------------------------------------------|
| `int insert(T entity);`                                                                     | 插入一条记录。                                                              |
| `T selectById(Serializable id);`                                                            | 根据 `ID` 查询。                                                          |
| `T selectOne(Wrapper<T> queryWrapper)`                                                      | 根据 `Wrapper` 条件，查询一条记录，例如 `qw.last("limit 1")` 限制取一条记录, 注意：多条数据会报异常。 |
| `List<T> selectBatchIds(Collection<? extends Serializable> idList);`                        | 查询（根据 `ID` 批量查询）。                                                    |
| `List<T> selectList(Wrapper<T> queryWrapper);`                                              | 根据 `Wrapper` 条件，查询全部记录。                                              |
| `List<T> selectByMap(Map<String, Object> columnMap);`                                       | 查询（根据 `columnMap` 条件）。                                               |
| `List<Map<String, Object>> selectMaps(Wrapper<T> queryWrapper);`                            | 根据 `Wrapper` 条件，查询全部记录，结果封装为 `Map`。                                  |
| `List<Object> selectObjs(Wrapper<T> queryWrapper);`                                         | 根据 `Wrapper` 条件，查询全部记录。注意：只返回第一个字段的值。                                |
| `<P extends IPage<T>> P selectPage(P page, Wrapper<T> queryWrapper);`                       | 根据 `Wrapper` 条件，查询全部记录（并翻页）。                                         |
| `<P extends IPage<Map<String, Object>>> P selectMapsPage(P page, Wrapper<T> queryWrapper);` | 根据 `Wrapper` 条件，查询全部记录（并翻页），结果封装为 `Map`。                             |
| `Long selectCount(Wrapper<T> queryWrapper);`                                                | 根据 `Wrapper` 条件，查询总记录数。                                              |
| `int updateById(T entity);`                                                                 | 根据 `ID` 修改。                                                          |
| `int update(T entity, Wrapper<T> updateWrapper);`                                           | 根据 `updateWrapper` 条件，更新记录。                                          |
| `int deleteById(Serializable id);`                                                          | 根据 `ID` 删除。                                                          |
| `int deleteById(T entity);`                                                                 | 根据实体 `ID` 删除。                                                        |
| `int delete(Wrapper<T> queryWrapper);`                                                      | 根据 `Wrapper` 条件，删除记录。                                                |
| `int deleteBatchIds(Collection<?> idList);`                                                 | 删除（根据 `ID` 或实体 批量删除）。                                                |
| `int deleteByMap(Map<String, Object> columnMap);`                                           | 根据 `columnMap` 条件，删除记录。                                              |

### `service & impl`

`UserService.java` 类：

```java
package com.sunchaser.chunyu.mybatisplus.service;

import entity.repository.io.github.llnancy.xuejian.mybatisplus.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * Mybatis-Plus 用户表 服务类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/11/01
 */
public interface UserService extends IService<UserEntity> {

}
```

`UserServiceImpl.java` 类：

```java
package com.sunchaser.chunyu.mybatisplus.service.impl;

import entity.repository.io.github.llnancy.xuejian.mybatisplus.UserEntity;
import mapper.repository.io.github.llnancy.xuejian.mybatisplus.UserMapper;
import service.io.github.llnancy.xuejian.mybatisplus.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Mybatis-Plus 用户表 服务实现类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/11/01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

}
```

接口继承 `com.baomidou.mybatisplus.extension.service.IService` 接口自动拥有 `service` 层通用 `crud`
能力，实现类继承 `com.baomidou.mybatisplus.extension.service.impl.ServiceImpl` 自动拥有通用 `crud` 能力实现。`service`
层通用 `crud` 能力和 `mapper` 层的主要区别在于对一些批量操作的 `@Transactional` 事务支持和条件构造器 `Wrapper`。

### `controller`

`UserController.java` 类：

```java
package com.sunchaser.chunyu.mybatisplus.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mybatis-Plus 用户表 Controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022-03-10
 */
@RestController
@RequestMapping("/user-entity")
public class UserController {

}
```

提供 `web` 访问入口。

## 基础配置

创建 `Spring Boot` 启动类 `ChunYuMybatisPlusApplication.java`，添加 `@MapperScan` 注解扫描 `Mapper` 文件。代码如下：

```java
package com.sunchaser.chunyu.mybatisplus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * mybatis-plus 启动器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/10
 */
@SpringBootApplication
@MapperScan("com.sunchaser.chunyu.mybatisplus.repository.mapper")
public class ChunYuMybatisPlusApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ChunYuMybatisPlusApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
```

创建 `resources/application.yml` 配置文件，添加数据库相关配置：

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/chunyu_mp?useUnicode=true&characterEncoding=UTF-8&useSSL=false
    username: root
    password: 123456
  mvc:
    hiddenmethod:
      filter:
        enabled: true # 开启HiddenHttpMethodFilter支持restful风格URL

# 打印执行sql，生产环境关闭
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 使用 `BaseMapper`

`BaseMapper` 在 `Spring` 环境下可直接进行依赖注入，也可通过 `com.baomidou.mybatisplus.extension.service.impl.ServiceImpl`
通用 `service` 实现类的 `getBaseMapper()` 方法获取。下面是基于 `Restful` 接口的简单运用示例：

```java
package com.sunchaser.chunyu.mybatisplus.web.controller;

import entity.repository.io.github.llnancy.xuejian.mybatisplus.UserEntity;
import service.io.github.llnancy.xuejian.mybatisplus.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Mybatis-Plus 用户表 Controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/11/01
 */
@RestController
@RequestMapping("/user-entity")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /* BaseMapper methods begin */

    /**
     * BaseMapper#insert：返回自增主键
     */
    @PostMapping("/user")
    public Long insert(@RequestBody UserEntity userEntity) {
        int affectedRows = userService.getBaseMapper().insert(userEntity);
        log.info("affectedRows={}", affectedRows);
        return userEntity.getId();
    }

    /**
     * BaseMapper#deleteById：根据 ID 删除
     */
    @DeleteMapping("/user/{id}")
    public void delete(@PathVariable Long id) {
        int affectedRows = userService.getBaseMapper().deleteById(id);
        log.info("affectedRows={}", affectedRows);
    }

    /**
     * BaseMapper#updateById：根据 ID 更新
     */
    @PatchMapping("/user/{id}")
    public void update(@PathVariable Long id, @RequestBody UserEntity userEntity) {
        userEntity.setId(id);
        int affectedRows = userService.getBaseMapper().updateById(userEntity);
        log.info("affectedRows={}", affectedRows);
    }

    /**
     * BaseMapper#selectById：根据 ID 查询
     */
    @GetMapping("/user/{id}")
    public UserEntity selectById(@PathVariable Long id) {
        return userService.getBaseMapper().selectById(id);
    }

    /**
     * BaseMapper#selectByIds：根据 ID 集合批量查询
     */
    @GetMapping("/users")
    public List<UserEntity> selectByIds(@RequestParam List<Long> ids) {
        return userService.getBaseMapper().selectBatchIds(ids);
    }

    /**
     * BaseMapper#selectByMap：根据 columnMap 多条件组合查询
     */
    @GetMapping("/users/selectByMap")
    public List<UserEntity> selectByMap(@RequestParam Map<String, Object> columnMap) {
        return userService.getBaseMapper().selectByMap(columnMap);
    }

    /**
     * BaseMapper#selectList：条件构造器查询
     */
    @GetMapping("/users/selectList")
    public List<UserEntity> selectList() {
        return userService.getBaseMapper().selectList(null);
    }
    /* BaseMapper methods end */
}
```

> 有关 `BaseMapper` 的更多使用方法可查看 [官方文档](https://baomidou.com/pages/49cc81/#mapper-crud-%E6%8E%A5%E5%8F%A3)

## 使用 `IService`

通用 `service` 实现的主要特性之一是事务性的批量新增修改删除操作，下面是简单使用：

```java
package com.sunchaser.chunyu.mybatisplus.web.controller;

import entity.repository.io.github.llnancy.xuejian.mybatisplus.UserEntity;
import service.io.github.llnancy.xuejian.mybatisplus.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mybatis-Plus 用户表 Controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/11/01
 */
@RestController
@RequestMapping("/user-entity")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    // 省略已有代码

    /* IService methods begin */

    /**
     * IService#count：查询总记录数
     */
    @GetMapping("/count")
    public Long count() {
        return userService.count();
    }

    /**
     * IService#saveBatch：批量新增
     */
    @PostMapping("/users")
    public List<Long> batchInsert(@RequestBody List<UserEntity> userEntityList) {
        boolean saveBatch = userService.saveBatch(userEntityList);
        log.info("saveBatch={}", saveBatch);
        return userEntityList.stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());
    }

    /**
     * IService#updateBatchById：批量更新
     */
    @PatchMapping("/users")
    public void batchUpdate(@RequestBody List<UserEntity> userEntityList) {
        boolean updateBatchById = userService.updateBatchById(userEntityList);
        log.info("updateBatchById={}", updateBatchById);
    }

    /**
     * IService#removeBatchByIds：根据 ID 集合批量删除
     */
    @DeleteMapping("/users")
    public void batchDelete(@RequestBody List<Long> idList) {
        boolean removeBatchByIds = userService.removeBatchByIds(idList);
        log.info("removeBatchByIds={}", removeBatchByIds);
    }
    /* IService methods end */
}
```

> 有关 `service` 的更多使用方法可查看 [官方文档](https://baomidou.com/pages/49cc81/#service-crud-%E6%8E%A5%E5%8F%A3)

## 条件构造器 `Wrapper`

通用 `service` 实现类的另外一个特性是条件构造器，功能强大，且支持链式调用。

先来看下 `Wrapper` 体系的整体类继承关系图：

![`Wrapper`](https://cdn.jsdelivr.net/gh/sunchaser-lilu/sunchaser-cdn@master/images/java-ee/mybatis-plus/wrapper.png)

将所有的查询条件分为四大类：

- `Compare`：值的比较。大于、小于、等于、`like` 及 `between` 等条件。
- `Join`：`SQL` 拼接。`SQL` 最前面、末尾、`or` 条件、`exists` 子句等。
- `Nested`：嵌套条件。相当于给 `SQL` 片段加上括号，提高执行的优先级。
- `Func`：各种 `SQL` 关键字子句。`is null`、`is not null`、`in`、`not in`、`group by`、`order by` 和 `having` 等。

`AbstractWrapper` 实现了上述四个接口，提供了查询条件封装生成 `SQL`
的基本能力。具体的条件构造器实现类又分为查询条件构造器 `QueryWrapper` 和修改条件构造器 `UpdateWrapper`
。另外还提供了 `Lambda` 版本支持链式调用的 `LambdaQueryWrapper` 和 `LambdaUpdateWrapper`。

通常查询删除使用 `QueryWrapper`，更新使用 `UpdateWrapper`。个人强烈推荐使用 `LambdaQueryWrapper` 和 `LambdaUpdateWrapper`
。下面是一些具体代码示例：

```java
package com.sunchaser.chunyu.mybatisplus.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import entity.repository.io.github.llnancy.xuejian.mybatisplus.UserEntity;
import service.io.github.llnancy.xuejian.mybatisplus.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * Mybatis-Plus 用户表 Controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/11/01
 */
@RestController
@RequestMapping("/user-entity")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    // 省略已有代码

    /* wrapper methods begin */

    /**
     * QueryWrapper 查询条件构造器，使用字段名字符串。
     * 查询 name 包含"龙"，age 在 18~25 之间，address 不为 null 且按 ID 降序排序的 user 信息
     * 强烈建议使用 LambdaQueryWrapper 函数式，可以避免字段名拼写错误等问题。
     */
    @GetMapping("/wrapper/users")
    public List<UserEntity> wrapperList() {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        // select：指定需要查询的字段
        queryWrapper.select("id", "name", "age", "address")
                .like("name", "龙")
                .between("age", 18, 25)
                .isNotNull("address")
                .orderByDesc("id");
        return userService.list(queryWrapper);
    }

    /**
     * LambdaQueryWrapper 函数式查询条件构造器
     * 查询 name 包含"龙"，age 在 18~25 之间，address 不为 null 且按 ID 降序排序的 user 信息
     */
    @GetMapping("/lambda-wrapper/users")
    public List<UserEntity> lambdaWrapperList() {
        LambdaQueryWrapper<UserEntity> lambdaQueryWrapper = new QueryWrapper<UserEntity>().lambda()
                .select(UserEntity::getId,
                        UserEntity::getName,
                        UserEntity::getAge,
                        UserEntity::getAddress
                ) // 指定需要查询的字段
                .like(UserEntity::getName, "龙")
                .between(UserEntity::getAge, 18, 25)
                .isNotNull(UserEntity::getAddress)
                .orderByDesc(UserEntity::getId);
        return userService.list(lambdaQueryWrapper);
    }

    /**
     * LambdaQueryWrapper 实现删除
     * 输入参数 id 不为 null 时进行删除
     */
    @DeleteMapping("/lambda-wrapper/user/{id}")
    public Boolean lambdaWrapperDelete(@PathVariable Long id) {
        LambdaQueryWrapper<UserEntity> lambdaQueryWrapper = new LambdaQueryWrapper<UserEntity>()
                .eq(Objects.nonNull(id), UserEntity::getId, id);
        return userService.remove(lambdaQueryWrapper);
    }

    /**
     * LambdaUpdateWrapper 实现修改
     * 可使用 Wrappers.<UserEntity>lambdaUpdate() 静态方法代替 new
     */
    @PatchMapping("/lambda-wrapper/users")
    public Boolean lambdaWrapperUpdate() {
        LambdaUpdateWrapper<UserEntity> lambdaUpdateWrapper = Wrappers.<UserEntity>lambdaUpdate()
                .eq(UserEntity::getAge, 18)
                .isNotNull(UserEntity::getAddress)
                .set(UserEntity::getName, "年轻人");
        return userService.update(lambdaUpdateWrapper);
    }
    /* wrapper methods end */
}
```

> 有关条件构造器的更多使用方法可查看 [官方文档](https://baomidou.com/pages/10c804/#abstractwrapper)

## 插件

基于原生 `MyBatis` 提供的插件机制进行实现。

### 分页插件

#### 配置类

配置 `com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor` 插件并注入到 `Spring`
容器中，同时可将启动类上的 `@MapperScan` 注解移至此方便统一管理。完整代码如下：

```java
package com.sunchaser.chunyu.mybatisplus.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 插件配置
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/13
 */
@Configuration
@MapperScan("com.sunchaser.chunyu.mybatisplus.repository.mapper")
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 分页查询
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return mybatisPlusInterceptor;
    }
}
```

### 使用分页

`BaseMapper` 提供的 `selectPage` 方法可直接支持分页，并支持使用条件构造器。使用示例如下：

```java
package com.sunchaser.chunyu.mybatisplus.web.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import entity.repository.io.github.llnancy.xuejian.mybatisplus.UserEntity;
import service.io.github.llnancy.xuejian.mybatisplus.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Mybatis-Plus 用户表 Controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/11/01
 */
@RestController
@RequestMapping("/user-entity")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    // 省略已有代码

    /* page query */

    /**
     * BaseMapper#selectPage：分页查询
     *
     * @param pageNo   当前页
     * @param pageSize 每页大小
     * @return 当前页数据
     */
    @GetMapping("/pageList")
    public List<UserEntity> pageList(@RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        Page<UserEntity> page = new Page<>(pageNo, pageSize);
        Page<UserEntity> userEntityPage = userService.getBaseMapper().selectPage(page, Wrappers.emptyWrapper());
        log.info("userEntityPage={}", JSONUtil.toJsonStr(userEntityPage));
        return userEntityPage.getRecords();
    }
}
```

## 其它

- [`IDEA` 插件 `MyBatisX`](https://baomidou.com/pages/ba5b24/)

完整代码可查看 [`Github`](https://github.com/sunchaser-lilu/sunchaser-chunyu/tree/master/chunyu-mybatis-plus)。
