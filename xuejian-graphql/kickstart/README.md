- [Spring Boot 整合 GraphQL](#spring-boot-整合-graphql)
- [创建工程](#创建工程)
- [第一个 GraphQL 查询](#第一个-graphql-查询)
  - [编写 Schema](#编写-schema)
  - [编写 Java Bean](#编写-java-bean)
  - [编写查询解析器](#编写查询解析器)
- [GraphQL IDE](#graphql-ide)
  - [Postman](#postman)
  - [Playground](#playground)
    - [初始化 Tab](#初始化-tab)
  - [GraphiQL](#graphiql)
  - [Altair](#altair)
- [Schema 最佳实践](#schema-最佳实践)
  - [面向对象](#面向对象)
  - [枚举](#枚举)
  - [注释](#注释)
  - [校验](#校验)
    - [非空](#非空)
    - [JSR303 校验](#jsr303-校验)
    - [最大查询深度](#最大查询深度)
- [服务端最佳实践](#服务端最佳实践)
  - [子解析器](#子解析器)
  - [全局异常处理](#全局异常处理)
  - [DataFetcherResult 包装返回结果](#datafetcherresult-包装返回结果)
  - [异步](#异步)
  - [Mutation](#mutation)
    - [编写 Schema](#编写-schema-1)
    - [编写 Java Bean](#编写-java-bean-1)
    - [编写 Mutation 解析器](#编写-mutation-解析器)
    - [客户端调用](#客户端调用)
  - [文件上传](#文件上传)
  - [`DataFetchingEnvironment`](#datafetchingenvironment)
  - [Scalar](#scalar)
    - [扩展 Scalar 类型](#扩展-scalar-类型)
    - [自定义 Scalar 类型](#自定义-scalar-类型)
  - [Listener 监听器](#listener-监听器)
  - [GraphQLContext 上下文](#graphqlcontext-上下文)
  - [Instrumentation](#instrumentation)
  - [Tracing 链路追踪](#tracing-链路追踪)
  - [Subscription 发布订阅](#subscription-发布订阅)
- [其它](#其它)
  - [IDEA 插件](#idea-插件)
  - [voyager](#voyager)

# Spring Boot 整合 GraphQL

核心依赖：

```xml
<!-- graphql starter -->
<dependency>
    <groupId>com.graphql-java-kickstart</groupId>
    <artifactId>graphql-spring-boot-starter</artifactId>
    <version>11.1.0</version>
</dependency>
```

# 创建工程

在 `xuejian-graphql` 模块中创建子模块 `kickstart`，引入 `Spring Boot` 及 `GraphQL` 等依赖，`pom.xml` 文件如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>io.github.llnancy</groupId>
        <artifactId>xuejian-graphql</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>kickstart</artifactId>
    <description>Spring Boot 整合 kickstart 框架</description>

    <properties>
        <graphql.starter.version>11.1.0</graphql.starter.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- spring boot -->
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
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!-- graphql starter -->
        <dependency>
            <groupId>com.graphql-java-kickstart</groupId>
            <artifactId>graphql-spring-boot-starter</artifactId>
            <version>${graphql.starter.version}</version>
        </dependency>
        <!-- graphql 单元测试 -->
        <dependency>
            <groupId>com.graphql-java-kickstart</groupId>
            <artifactId>graphql-spring-boot-starter-test</artifactId>
            <version>${graphql.starter.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

创建 `Spring Boot` 启动类 `XueJianKickStartGraphQLApplication.java`：

```java
package io.github.llnancy.xuejian.graphql.kickstart;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * kickstart spring boot 启动类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/9
 */
@SpringBootApplication
public class XueJianKickStartGraphQLApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(XueJianKickStartGraphQLApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
```

# 第一个 GraphQL 查询

默认 `GraphQL` 的 `Schema` 文件都存放在 `classpath` 类路径下，文件后缀名为 `.graphqls`。

```yml
graphql:
  tools:
    schema-location-pattern: "**/*.graphqls" # graphql schema location
```

下面以查询用户 `User` 为例创建第一个 `GraphQL Schema`。

按照约定，我们在 `resources` 目录下创建 `graphql/query.graphqls` 文件，它被称为 `GraphQL` 查询文件，之后所有的查询都将写在该文件中。

## 编写 Schema

第一个查询 `Schema` 写法如下：

```graphql
type Query {
    user(id: ID): User
}
```

> 语法解读：首先 `type: Query {}` 定义了该模式的类型是 `Query` 查询，然后 `user(id: ID): User` 表示一个方法，接收一个类型为 `ID` 的 `id` 参数，返回一个 `User` 对象。

由于 `User` 暂不存在，所以 `IDE` 暂时报错，我们创建 `graphql/user/user.graphqls` 文件，编写内容如下：

```graphql
type User {
    id: ID
    name: String
    sex: String
    age: Int
    address: String
}
```

创建自定义的 `type` 类型 `User`，包含五个字段。

## 编写 Java Bean

每一个自定义 `type` 都需要一个 `Java` 类与之对应。创建 `User.java` 类，代码如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.model;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/**
 * User.java -> user.graphqls
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/9
 */
@Value
@Builder
public class User {
    
    UUID id;
    
    String name;
    
    String sex;
    
    Integer age;
    
    String address;
}
```

> `lombok` 的 `@Value` 注解将类和属性声明为 `final` 并提供属性的 `getter` 方法；`@Builder` 注解提供建造者模式。

## 编写查询解析器

每个 `GraphQL` 查询都需要有一个与之匹配的查询解析器 `GraphQLQueryResolver`。

创建 `UserQueryResolver.java` 类，代码如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.resolver.user.query;

import io.github.llnancy.xuejian.graphql.kickstart.model.User;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * graphql user query resolver
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/9
 */
@Component
@Slf4j
public class UserQueryResolver implements GraphQLQueryResolver {

    public User user(String id) {
        log.info("Retrieving user id: {}", id);
        return User.builder()
                .id(UUID.randomUUID())
                .name("SunChaser")
                .sex("男")
                .age(18)
                .address("HangZhou China")
                .build();
    }
}
```

方法 `user` 匹配 `query.graphqls` 文件中的 `user`。

> 这里方法 `user` 的入参 `id` 可以指定为 `UUID` 类型，也可用 `String` 类型来兼容。

至此，我们的第一个 `GraphQL` 查询代码就编写完成。

# GraphQL IDE

`GraphQL` 服务端编写完成后我们需要用客户端进行调用，最简单的方式我们可以用 `Postman` 进行调用，当然 `GraphQL` 社区也提供了一些高效的 `IDE` 工具供我们选择，例如 `GraphiQL`、`Playground` 及 `Altair` 等。

一个典型的 `GraphQL` 查询写法如下：

```graphql
{
  user(id: "be4af231-fcd3-4ed4-bcf3-505247197dfa") {
    id
    name
    sex
    age
  }
}
```

语法解读：首先用一个花括号 `{}` 包裹整个语句，然后 `user` 对应着 `query.graphqls` 中的 `user`，入参 `id` 是一个 `UUID`，最后需要查询 `id`、`name`、`sex` 和 `age` 这四个字段。当然我们可根据实际情况增加或减少需要查询的字段。

## Postman

`Postman` 中的查询示例如下：

![image-20220509195320767](https://posts-cdn.lilu.org.cn/2022/05/09194d8923a1312af468fe167d4b22458b532f15.png)

## Playground

项目地址：[https://github.com/graphql/graphql-playground](https://github.com/graphql/graphql-playground)

`pom.xml` 依赖：

```xml
<!-- graphql 的一个网页 IDE -->
<dependency>
    <groupId>com.graphql-java-kickstart</groupId>
    <artifactId>playground-spring-boot-starter</artifactId>
    <version>${graphql.starter.version}</version>
    <scope>runtime</scope>
</dependency>
```

默认访问路径为 [http://localhost:8080/playground](http://localhost:8080/playground)。查询示例如下：

![image-20220510115358294](https://posts-cdn.lilu.org.cn/2022/05/1011798c3ace501a615e5fdd6f3f3029dfecef5c.png)

可用的配置项有：

```yml
graphql:
  playground:
    mapping: /playground
    endpoint: /graphql
    subscriptionEndpoint: /subscriptions
    staticPath.base: my-playground-resources-folder
    enabled: true
    pageTitle: Playground
    cdn:
      enabled: false
      version: latest
    settings:
      editor.cursorShape: line
      editor.fontFamily: "'Source Code Pro', 'Consolas', 'Inconsolata', 'Droid Sans Mono', 'Monaco', monospace"
      editor.fontSize: 14
      editor.reuseHeaders: true
      editor.theme: dark
      general.betaUpdates: false
      prettier.printWidth: 80
      prettier.tabWidth: 2
      prettier.useTabs: false
      request.credentials: omit
      schema.polling.enable: true
      schema.polling.endpointFilter: "*localhost*"
      schema.polling.interval: 2000
      schema.disableComments: true
      tracing.hideTracingResponse: true
    headers:
      headerFor: AllTabs
    tabs:
      - name: Example Tab
        query: classpath:exampleQuery.graphql
        headers:
          SomeHeader: Some value
        variables: classpath:variables.json
        responses:
          - classpath:exampleResponse1.json
          - classpath:exampleResponse2.json
```

### 初始化 Tab

`Playground` 可以在启动时初始化 `Tab`，用来提供一些查询示例。

```yml
graphql:
  playground:
    headers:
      Authorization: SunChaser
    tabs:
      - name: User sample query
        query: classpath:playground/user.graphql
        variables: classpath:playground/user-variables.json
```

`playground/user.graphql`:

```graphql
# Write your query or mutation here
query GET_USER($id: ID!) {
    user(id: $id) {
        id
        name
        age
        address {
            province
            city
        }
        createdOn
        createdAt
    }
}

mutation CREATE_USER($name: String!) {
  createUser(input: {
    name: $name
    age: 10
    sex: MAN
    address: {
      province: "Zhe Jiang"
      city: "Hang Zhou"
    }
  }) {
    id
    name
  }
}
```

`playground/user-variables.json`:

```json
{
  "id": "c508301f-ba8c-4907-a5da-990b6612e560",
  "name": "SunChaser"
}
```

## GraphiQL

项目地址：[https://github.com/graphql/graphiql](https://github.com/graphql/graphiql)

`pom.xml` 依赖：

```xml
<!-- graphiql 和 playground 类似 -->
<dependency>
    <groupId>com.graphql-java-kickstart</groupId>
    <artifactId>graphiql-spring-boot-starter</artifactId>
    <version>${graphql.starter.version}</version>
</dependency>
```

默认访问路径为 [http://localhost:8080/graphiql](http://localhost:8080/graphiql)。查询示例如下：

![image-20220510141439077](https://posts-cdn.lilu.org.cn/2022/05/10140f5d1143a97b965fb66b4fd5c8c01ec22cf2.png)

可用的配置项有：

```yml
graphql:
  graphiql:
    mapping: /graphiql
    endpoint:
      graphql: /graphql
      subscriptions: /subscriptions
    subscriptions:
      timeout: 30
      reconnect: false
    basePath: /
    enabled: true
    pageTitle: GraphiQL
    cdn:
      enabled: false
      version: latest
    props:
      resources:
        query: query.graphql
        defaultQuery: defaultQuery.graphql
        variables: variables.json
      variables:
        editorTheme: "solarized light"
    headers:
      Authorization: "Bearer <your-token>"
```

## Altair

项目地址：[https://github.com/altair-graphql/altair](https://github.com/altair-graphql/altair)

`pom.xml` 依赖：

```xml
<!-- altair -->
<dependency>
    <groupId>com.graphql-java-kickstart</groupId>
    <artifactId>altair-spring-boot-starter</artifactId>
    <version>${graphql.starter.version}</version>
</dependency>
```

默认访问路径为[`http://localhost:8080/altair`](http://localhost:8080/altair)。查询示例如下：

![image-20220510142821995](https://posts-cdn.lilu.org.cn/2022/05/10142a40f819cb57701ffc8fca3906724e7d202d.png)

可用的配置项有：

```yml
graphql:
  altair:
    enabled: true
    mapping: /altair
    subscriptions:
      timeout: 30
      reconnect: false
    static:
      base-path: /
    page-title: Altair
    cdn:
      enabled: false
      version: 4.0.2
    options:
      endpoint-url: /graphql
      subscriptions-endpoint: /subscriptions
      initial-settings:
        theme: dracula
      initial-headers:
        Authorization: "Bearer <your-token>"
    resources:
      initial-query: defaultQuery.graphql
      initial-variables: variables.graphql
      initial-pre-request-script: pre-request.graphql
      initial-post-request-script: post-request.graphql
```

# Schema 最佳实践

下面是一些 `Schema` 设计的最佳实践。

## 面向对象

尽量采用面向对象化的 `Schema` 设计。例如 `User` 中的 `address`，它可以拆分为省、市、区及详细地址，所以我们最好将它设计为一个单独的 `Schema`，这样会更面向对象。创建 `graphql/user/address.graphqls` 文件，内容如下：

```graphql
type Address {
    province: String
    city: String
    area: String
    detailAddress: String
}
```

然后将 `user.graphqls` 中的 `address` 字段类型修改为 `Address`，同时 `Java` 类 `User` 中的 `address` 字段也要同步修改为 `Address` 类，创建 `Address` 类如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Address.java -> address.graphqls
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    
    private String province;
    
    private String city;
    
    private String area;
    
    private String detailAddress;
}
```

## 枚举

对于一些取值范围有限的字段，优先使用枚举类型 `Schema`。例如 `User` 中的 `sex`，它仅有“男”和“女”两个取值，创建 `graphql/user/sex.graphqls` 文件，内容如下：

```graphql
enum Sex {
    MAN
    WOMAN
}
```

然后将 `user.graphqls` 中的 `sex` 字段类型修改为 `Sex`，同时 `Java` 类 `User` 中的 `sex` 字段也要同步修改为 `SexEnum` 枚举，创建 `SexEnum` 枚举类如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.model;

/**
 * SexEnum.java -> sex.graphqls
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/10
 */
public enum SexEnum {

    MAN,

    WOMAN,
    ;
}
```

## 注释

写注释是一个良好的素养。`.graphql` 文件中的注释以 `#` 开头，例如：

```graphql
# All available queries on this graphql server
type Query {
    # 根据 ID 查询用户信息
    user(id: ID): User
}
```

## 校验

`Schema` 中的方法的入参出参等可以进行一些基本规则校验。

### 非空

方法的入参出参、自定义类型中的字段可以指定为非空（不能为 `null`）。类型后面加英文叹号 `!`，例如：

```graphql
# All available queries on this graphql server
type Query {
    # 根据 ID 查询用户信息
    user(id: ID!): User!
}
```

```graphql
type User {
    id: ID!
    name: String!
    sex: Sex
    age: Int
    address: Address
}
```

### JSR303 校验

`pom.xml` 依赖：

```xml
<!-- JSR303 校验 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

可以和 `Spring MVC` 一样用 `JSR303` 规范中的注解来校验 `Bean`。使用示例如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.resolver.user.query;

import io.github.llnancy.xuejian.graphql.kickstart.model.Address;
import io.github.llnancy.xuejian.graphql.kickstart.model.SexEnum;
import io.github.llnancy.xuejian.graphql.kickstart.model.User;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

/**
 * graphql user resolver
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/9
 */
@Component
@Slf4j
@Validated
public class UserQueryResolver implements GraphQLQueryResolver {

    public User user(@NotBlank String id) {
        log.info("Retrieving user id: {}", id);
        return User.builder()
                .id(UUID.randomUUID())
                .name("SunChaser")
                .sex(SexEnum.MAN)
                .age(18)
                .address(Address.builder()
                        .province("ZheJiang")
                        .city("HangZhou")
                        .area("BinJiang")
                        .detailAddress("SunChaser")
                        .build()
                )
                .build();
    }
}
```

### 最大查询深度

某些情况下 `Schema` 可能会出现类型嵌套的情况。以 `user.graphqls` 为例，假设一个 `User` 有一个“儿子”，即：

```graphql
type User {
    id: ID!
    name: String!
    sex: Sex
    age: Int
    address: Address
    son: User
}
```

```java
package io.github.llnancy.xuejian.graphql.kickstart.model;

import io.github.llnancy.xuejian.graphql.kickstart.model.Address;
import io.github.llnancy.xuejian.graphql.kickstart.model.SexEnum;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/**
 * User.java -> user.graphqls
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/9
 */
@Value
@Builder
public class User {

    UUID id;
    
    String name;
    
    SexEnum sex;
    
    Integer age;
    
    Address address;
    
    User son;
}
```

这时查询就会有一个查询深度的概念。例如以下查询：

```graphql
{
  user(id: "be4af231-fcd3-4ed4-bcf3-505247197dfa") {
    id
    name
    son {
      id
      name
      son {
        id
        name
        son {
          id
          name
          son {
            id
            name
            son {
              id
              name
              # ...无限嵌套
            }
          }
        }
      }
    }
  }
}
```

正所谓”子子孙孙无穷匮也“，如果服务端不加以限制，内存迟早溢出。

我们可以通过 `graphql.servlet.max-query-depth` 配置项设置最大查询深度。例如：

```yml
graphql:
  servlet:
    max-query-depth: 13
```

> 建议设置为 `13` 或以上。因为一些 `GraphQL IDE` 在进行心跳探活时的查询深度会达到 `13`，比如 `playground`。

# 服务端最佳实践

## 子解析器

实际业务中我们的数据可能来源于不同的下游微服务。以 `User` 为例，姓名性别等基本信息来源于用户微服务，而地址信息来源于地址微服务。这时我们就可以将地址信息的查询放在一个单独的子解析器中。

创建 `AddressResolver.java` 类代码如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.resolver.user;

import io.github.llnancy.xuejian.graphql.kickstart.model.Address;
import io.github.llnancy.xuejian.graphql.kickstart.model.User;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * graphql address resolver
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/10
 */
@Component
@Slf4j
public class AddressResolver implements GraphQLResolver<User> {

    public Address address(User user) {
        log.info("Retrieving address data for user id: {}", user.getId());
        return Address.builder()
                .province("ZheJiang")
                .city("HangZhou")
                .area("BinJiang")
                .detailAddress("SunChaser")
                .build();
    }
}
```

`AddressResolver` 类实现了 `graphql.kickstart.tools.GraphQLResolver` 接口，泛型声明为 `User`，方法名 `address` 对应着 `user.graphqls` 中的 `address` 字段名，方法入参 `User` 会由 `kickstart` 框架在运行时自动注入，方法的返回值是一个 `Address` 对象。

于是，我们就可以将 `UserQueryResolver` 中查询 `address` 的部分移动到 `AddressResolver`。`AddressResolver` 称为一个子解析器。

客户端查询示例如下：

```graphql
{
  user(id: "be4af231-fcd3-4ed4-bcf3-505247197dfa") {
    id
    name
    address {
      province
      city
    }
  }
}
```

## 全局异常处理

当 `GraphQL` 查询解析器抛出异常时，`kickstart` 框架默认的全局异常处理器 `DefaultGraphQLErrorHandler` 会返回固定的异常信息 `Internal Server Error(s) while executing query`。但实际业务开发中我们更希望能返回自定义的异常信息，所以需要自定义一个全局异常处理器。`kickstart` 框架支持 `Spring MVC` 形式的全局异常处理。

开启全局异常处理：

```yml
graphql:
  servlet:
    exception-handlers-enabled: true
```

创建全局异常处理器 `GraphQLExceptionHandler`，代码如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.exceptions;

import graphql.GraphQLException;
import graphql.kickstart.spring.error.ThrowableGraphQLError;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理器
 *
 * @author sunchaser admin@lilu.org.cn
 * @see graphql.kickstart.execution.error.DefaultGraphQLErrorHandler
 * @since JDK8 2022/5/6
 */
@Component
public class GraphQLExceptionHandler {

    @ExceptionHandler({GraphQLException.class, ConstraintViolationException.class})
    public ThrowableGraphQLError handle(Exception e) {
        return new ThrowableGraphQLError(e);
    }

    @ExceptionHandler(RuntimeException.class)
    public ThrowableGraphQLError handle(RuntimeException e) {
        return new ThrowableGraphQLError(e, "Internal Server Error");
    }
}
```

使用 `Spring MVC` 中的 `@ExceptionHandler` 注解来处理异常，将异常包装为 `ThrowableGraphQLError` 对象，对于 `GraphQLException` 和 `ConstraintViolationException` 异常来说我们返回原异常中携带的错误信息，除此之外的其它 `RuntimeException` 异常我们用固定字符串 `Internal Server Error` 来防止异常信息外泄。

## DataFetcherResult 包装返回结果

`DataFetcherResult` 包含解析器正常返回的数据 `data` 和错误列表 `errors`。使用示例如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.resolver.user;

import io.github.llnancy.xuejian.graphql.kickstart.model.Address;
import io.github.llnancy.xuejian.graphql.kickstart.model.User;
import graphql.execution.DataFetcherResult;
import graphql.kickstart.execution.error.GenericGraphQLError;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * graphql address resolver
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/10
 */
@Component
@Slf4j
public class AddressResolver implements GraphQLResolver<User> {

    public DataFetcherResult<Address> address(User user) {
        log.info("Retrieving address data for user id: {}", user.getId());

        // throw new GraphQLException("SQL Error");
        // throw new RuntimeException("SQL Error");

        return DataFetcherResult.<Address>newResult()
                .data(Address.builder()
                        .province("ZheJiang")
                        .city("HangZhou")
                        .area("BinJiang")
                        .detailAddress("SunChaser")
                        .build())
                .error(new GenericGraphQLError("get address error"))
                .build();
    }
}
```

## 异步

默认情况下，每个解析器都是同步执行的。为了提高效率，我们可以进行异步处理，让解析器返回一个 `CompletableFuture` 对象。代码示例如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.resolver.user;

import io.github.llnancy.xuejian.graphql.kickstart.model.Address;
import io.github.llnancy.xuejian.graphql.kickstart.model.User;
import graphql.execution.DataFetcherResult;
import graphql.kickstart.execution.error.GenericGraphQLError;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * graphql address resolver
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/10
 */
@Component
@Slf4j
public class AddressResolver implements GraphQLResolver<User> {

    private final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public CompletableFuture<DataFetcherResult<Address>> address(User user) {
        return CompletableFuture.supplyAsync(
                () -> {
                    log.info("Retrieving address data for user id: {}", user.getId());
                    return DataFetcherResult.<Address>newResult()
                            .data(Address.builder()
                                    .province("ZheJiang")
                                    .city("HangZhou")
                                    .area("BinJiang")
                                    .detailAddress("SunChaser")
                                    .build())
                            .error(new GenericGraphQLError("get address error"))
                            .build();
                },
                EXECUTOR
        );
    }
}
```

## Mutation

除了查询，`GraphQL` 还支持更新服务端的数据，包括新增、修改和删除，这统一称为突变 `Mutation`。

在 `resources` 目录下创建 `graphql/mutation.graphqls` 文件，之后所有的 `Mutation` 都将写在该文件中。下面以创建 `User` 为例创建一个 `Mutation`。

### 编写 Schema

`graphql/mutation.graphqls` 代码如下：

```graphql
# All mutations available in graphql
type Mutation {
    # Create a user
    createUser(input: CreateUserInput!): User!
}
```

接收一个非空输入参数 `CreateUserInput`，返回一个非空 `User` 对象。

创建 `graphql/user/input/createUserInput.graphqls` 文件，编写代码如下：

```graphql
input CreateUserInput {
    name: String!
    sex: Sex
    age: Int
    address: AddressInput!
}

input AddressInput {
    province: String
    city: String
    area: String
    detailAddress: String
}
```

由于 `CreateUserInput` 的类型是 `input`，它包含的字段不能是 `type` 类型，所以 `address.graphqls` 无法被复用，这里声明了一个 `input AddressInput`，字段完全一致。

### 编写 Java Bean

创建 `CreateUserInput` 类，代码如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.model.input;

import io.github.llnancy.xuejian.graphql.kickstart.model.Address;
import io.github.llnancy.xuejian.graphql.kickstart.model.SexEnum;
import lombok.Data;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/11
 */
@Data
public class CreateUserInput {

    private String name;
    
    private SexEnum sex;
    
    private Integer age;
    
    private Address address;
}
```

这里的 `Address.java` 类可以进行复用，注意需要让其具有无参构造函数。

### 编写 Mutation 解析器

`Mutation` 对应的解析器是 `GraphQLMutationResolver`。

创建 `UserMutation` 类，实现 `GraphQLMutationResolver` 接口，代码示例如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.resolver.user.mutation;

import io.github.llnancy.xuejian.graphql.kickstart.model.User;
import io.github.llnancy.xuejian.graphql.kickstart.model.input.CreateUserInput;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.UUID;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/11
 */
@Component
@Slf4j
@Validated
public class UserMutation implements GraphQLMutationResolver {

    public User createUser(@Valid CreateUserInput input) {
        log.info("Creating user for {}", input.getName());

        return User.builder()
                .id(UUID.randomUUID())
                .name(input.getName())
                .sex(input.getSex())
                .age(input.getAge())
                .address(input.getAddress())
                .build();
    }
}
```

方法名 `createUser` 匹配 `mutation.graphqls` 中的 `createUser`，入参为 `CreateUserInput`，这里也可以和 `Spring MVC` 一样使用 `JSR303` 注解校验。

### 客户端调用

客户端中可以指定 `Schema` 的类型并取名以便同时存在多个 `Schema`。以 `Playground` 为例，使用示例如下：

![image-20220511211036181](https://posts-cdn.lilu.org.cn/2022/05/112112df2897bddc2a6e04db957e115f6edc95ea.png)

```graphql
# Write your query or mutation here
query GET_USER {
  user(id: "be4af231-fcd3-4ed4-bcf3-505247197dfa") {
    id
    name
    address {
      province
    }
  }
}
mutation CREATE_USER {
  createUser(input: {
    name: "SunChaser"
    age: 10
    sex: MAN
    address: {
      province: "Zhe Jiang"
      city: "Hang Zhou"
    }
  }) {
    id
    name
  }
}
```

## 文件上传

在 `graphql/mutation.graphqls` 中添加文件上传 `Schema` 如下：

```graphql
# Upload a file
uploadFile: ID!
```

创建文件上传解析器 `UploadFileMutation` 类，代码如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.resolver.user.mutation;

import graphql.kickstart.servlet.context.DefaultGraphQLServletContext;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.Part;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * graphql upload file mutation resolver
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/12
 */
@Component
@Slf4j
public class UploadFileMutation implements GraphQLMutationResolver {

    public String uploadFile(DataFetchingEnvironment environment) {
        log.info("Uploading file");

        // 获取graphql Servlet上下文
        DefaultGraphQLServletContext context = environment.getContext();

        // 获取文件对象javax.servlet.http.Part
        List<Part> fileParts = context.getFileParts();

        fileParts.forEach(part -> {
            // part.getInputStream();
            log.info("uploading: {}, size: {}", part.getSubmittedFileName(), part.getSize());
        });

        return UUID.randomUUID().toString();
    }
}
```

用 `Postman` 调用示例如下：

![image-20220512145749326](https://posts-cdn.lilu.org.cn/2022/05/12144277c27e4e9caca64b5f563ab9a262d6ce53.png)

## `DataFetchingEnvironment`

`Environment` 环境，能获取很多与 `GraphQL` 请求相关的信息，可将其指定为 `Resolver` 解析器方法的最后一个参数，`kickstart` 框架会自动注入。使用示例如下：

```java
public User createUser(@Valid CreateUserInput input, DataFetchingEnvironment environment) {
    log.info("Creating user. input: {}", input);

    // 请求中包含的需要查询的字段集合
    DataFetchingFieldSelectionSet selectionSet = environment.getSelectionSet();

    // 获取所有查询字段
    List<String> fieldNames = selectionSet.getFields()
            .stream()
            .map(SelectedField::getName)
            .collect(Collectors.toList());

    // 查询字段中是否包含id
    boolean containsId = selectionSet.contains("id");

    // 查询字段中是否同时包含id和name
    boolean containsAllOfIdAndName = selectionSet.containsAllOf("id", "name");

    // 查询字段中是否包含id或name（任意一个）
    boolean containsAnyOfIdAndName = selectionSet.containsAnyOf("id", "name");

    // 上下文
    DefaultGraphQLServletContext context =  environment.getContext();

    // Servlet API
    HttpServletRequest request = context.getHttpServletRequest();
    HttpServletResponse response = context.getHttpServletResponse();

    return User.builder()
            .id(UUID.randomUUID())
            .name(input.getName())
            .sex(input.getSex())
            .age(input.getAge())
            .address(input.getAddress())
            .build();
}
```

## Scalar

在 `GraphQL` 的类型系统中，查询的叶子节点称为 `Scalar` 标量。

`GraphQL` 规范中只定义了五种标量类型：

- `String`：字符串。
- `Boolean`：布尔值。
- `Int`：带符号的 `32` 位整数。
- `Float`：带符号的双精度浮点数。
- `ID`：唯一 `ID`，序列化方式与字符串相同。

`graphql-java` 类库在规范的基础上扩展了以下六种 `Scalar` 标量类型：

- `Long`：`java.lang.Long`
- `Short`：`java.lang.Short`
- `Byte`：`java.lang.Byte`
- `BigDecimal`：`java.math.BigDecimal`
- `BigInteger`：`java.math.BigInteger`
- `Char`：`java.lang.Character`

可在 `graphql.Scalars` 类中查看所有系统标量类型的实例。

### 扩展 Scalar 类型

引入依赖：

```xml
<dependency>
    <groupId>com.graphql-java</groupId>
    <artifactId>graphql-java-extended-scalars</artifactId>
    <version>${graphql.extended.scalars.version}</version>
    <exclusions>
        <exclusion>
            <groupId>com.graphql-java</groupId>
            <artifactId>graphql-java</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

向 `Spring` 中注入需要使用的扩展 `Scalar` 类型：

```java
package io.github.llnancy.xuejian.graphql.kickstart.config;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Extend Scalar Configuration
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/16
 */
@Configuration
public class ScalarConfig {
    
    @Bean
    public GraphQLScalarType nonNegativeInt() {
        return ExtendedScalars.NonNegativeInt;
    }

    @Bean
    public GraphQLScalarType date() {
        return ExtendedScalars.Date;
    }

    @Bean
    public GraphQLScalarType dateTime() {
        return ExtendedScalars.DateTime;
    }
}
```

可在 `graphql.scalars.ExtendedScalars` 类中查看该库的所有扩展 `Scalar` 类型。

使用示例：

`graphql/query.graphqls`

```graphql
# @see graphql.scalars.ExtendedScalars
scalar NonNegativeInt
scalar Date
scalar DateTime

# All available queries on this graphql server
type Query {
    # 根据ID查询用户信息
    user(id: ID!): User!
}
```

`graphql/user.graphqls`

```graphql
type User {
    id: ID!
    name: String!
    sex: Sex
    age: NonNegativeInt
    address: Address
    son: User
    createdOn: Date
    createdAt: DateTime
}
```

`User.java`

```java
package io.github.llnancy.xuejian.graphql.kickstart.model;

import io.github.llnancy.xuejian.graphql.kickstart.model.Address;
import io.github.llnancy.xuejian.graphql.kickstart.model.SexEnum;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * User.java -> user.graphqls
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/9
 */
@Value
@Builder
public class User {

    UUID id;
    
    String name;
    
    SexEnum sex;
    
    Integer age;
    
    Address address;
    
    User son;
    
    LocalDate createdOn;
    
    ZonedDateTime createdAt;
}
```

`Date` 对应的 `Java` 类型是 `LocalDate`，`DateTime` 对应的 `Java` 类型是 `ZonedDateTime`。

> `graphql-java-extended-scalars` 包里面的扩展 `Scalar` 无 `Java8` 的 `LocalDateTime`。

### 自定义 Scalar 类型

以 `Java8` 的 `LocalDateTime` 为例。

自定义 `Scalar` 示例代码如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.scalars.datetime;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import static graphql.scalars.util.Kit.typeName;

/**
 * LocalDateTime Scalar
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/18
 */
public class LocalDateTimeScalar {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final GraphQLScalarType INSTANCE;

    private LocalDateTimeScalar() {
    }

    static {
        INSTANCE = GraphQLScalarType.newScalar()
                .name("LocalDateTime")
                .description("An implementation of Java8 LocalDateTime Scalar")
                .coercing(new Coercing<LocalDateTime, Object>() {

                    @Override
                    public Object serialize(Object dataFetcherResult) throws CoercingSerializeException {
                        return serializeLocalDateTime(dataFetcherResult);
                    }

                    @Override
                    public LocalDateTime parseValue(Object input) throws CoercingParseValueException {
                        return parseLocalDateTimeFromVariable(input);
                    }

                    @Override
                    public LocalDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
                        return parseLocalDateTimeFromAstLiteral(input);
                    }
                })
                .build();
    }

    private static Object serializeLocalDateTime(Object dataFetcherResult) {
        LocalDateTime localDateTime;
        if (dataFetcherResult instanceof LocalDateTime) {
            localDateTime = (LocalDateTime) dataFetcherResult;
        } else {
            throw new CoercingSerializeException(
                    "Expected something we can convert to 'java.time.LocalDateTime' but was '" + typeName(dataFetcherResult) + "'."
            );
        }
        try {
            return FORMATTER.format(localDateTime);
        } catch (Exception e) {
            throw new CoercingSerializeException(
                    "Unable to turn TemporalAccessor into LocalDateTime because of : '" + e.getMessage() + "'."
            );
        }
    }

    private static LocalDateTime parseLocalDateTimeFromVariable(Object input) {
        LocalDateTime localDateTime;
        if (input instanceof LocalDateTime) {
            localDateTime = (LocalDateTime) input;
        } else if (input instanceof String) {
            localDateTime = parseLocalDateTime(input.toString(), CoercingParseValueException::new);
        } else {
            throw new CoercingParseValueException(
                    "Expected a 'String' but was '" + typeName(input) + "'."
            );
        }
        return localDateTime;
    }

    private static LocalDateTime parseLocalDateTimeFromAstLiteral(Object input) {
        if (!(input instanceof StringValue)) {
            throw new CoercingParseLiteralException(
                    "Expected AST type 'StringValue' but was '" + typeName(input) + "'."
            );
        }
        return parseLocalDateTime(((StringValue) input).getValue(), CoercingParseLiteralException::new);
    }

    private static LocalDateTime parseLocalDateTime(String s, Function<String, RuntimeException> exceptionMaker) {
        try {
            return LocalDateTime.parse(s, FORMATTER);
        } catch (Exception e) {
            throw exceptionMaker.apply("Invalid LocalDateTime value : '" + s + "'. because of : '" + e.getMessage() + "'");
        }
    }
}
```

`Scalar` 中真正起作用的是 `graphql.schema.Coercing` 接口的实现，它包含以下三个方法：

- `parseValue`：将输入变量转化为 `Java` 对象。
- `parseLiteral`：将输入的 `AST` 文字（`graphql.language.Value`）转化为 `Java` 对象。
- `serialize`：将一个 `Java` 对象转化为该 `Scalar` 类型需要输出的形式。

看下面这个 `mutation`：

```graphql
mutation CREATE_USER($name: String!, $createdOn: LocalDateTime) {
    createUser(input: {
        name: $name
        age: 10
        sex: MAN
        createdOn: $createdOn
        createdAt: "2022-05-18 20:44:37"
        address: {
            province: "Zhe Jiang"
            city: "Hang Zhou"
        }
    }) {
        id
        name
        createdOn
        createdAt
    }
}
```

三个方法的调用时机分别为：

- `parseValue`：当将 `$createdOn` 转化为 `LocalDateTime` 对象时调用。
- `parseLiteral`：当将 `"2022-05-18 20:44:37"` 转化为 `LocalDateTime` 对象时被调用。
- `serialize`：当 `createdOn` 字段被查询输出时调用。

## Listener 监听器

和 `Servlet` 类似，`GraphQL` 请求也支持监听器机制，只需实现 `GraphQLServletListener` 接口。

以记录请求耗时为例，代码示例如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.listener;

import graphql.kickstart.servlet.core.GraphQLServletListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * GraphQL Servlet Listener
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/18
 */
@Component
@Slf4j
public class LoggingListener implements GraphQLServletListener {

    @Override
    public RequestCallback onRequest(HttpServletRequest request, HttpServletResponse response) {
        LocalDateTime startTime = LocalDateTime.now();
        log.info("Received graphql request");
        return new GraphQLServletListener.RequestCallback() {

            @Override
            public void onSuccess(HttpServletRequest request, HttpServletResponse response) {
                // no-op
            }

            @Override
            public void onError(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
                // no-op
            }

            @Override
            public void onFinally(HttpServletRequest request, HttpServletResponse response) {
                log.info("Completed Request. Time Taken: {}", Duration.between(startTime, LocalDateTime.now()));
            }
        };
    }
}
```

## GraphQLContext 上下文

前面我们用 `DataFetchingEnvironment#getContext` 方法获取过 `GraphQL` 请求的默认上下文 `DefaultGraphQLServletContext`，它能在所有解析器中使用，并且支持自定义。

以获取 `HTTP Header` 中的 `user_id` 字段为例，使用静态代理设计模式自定义上下文。代码示例如下：

`CustomGraphQLContext.java` 自定义上下文：

```java
package io.github.llnancy.xuejian.graphql.kickstart.context;

import graphql.kickstart.servlet.context.GraphQLServletContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.dataloader.DataLoaderRegistry;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 自定义 GraphQL Servlet 上下文
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/18
 */
@Getter
@AllArgsConstructor
public class CustomGraphQLContext implements GraphQLServletContext {

    private final String userId;

    private final GraphQLServletContext context;

    @Override
    public List<Part> getFileParts() {
        return context.getFileParts();
    }

    @Override
    public Map<String, List<Part>> getParts() {
        return context.getParts();
    }

    @Override
    public HttpServletRequest getHttpServletRequest() {
        return context.getHttpServletRequest();
    }

    @Override
    public HttpServletResponse getHttpServletResponse() {
        return context.getHttpServletResponse();
    }

    @Override
    public Optional<Subject> getSubject() {
        return context.getSubject();
    }

    @Override
    public @NonNull DataLoaderRegistry getDataLoaderRegistry() {
        return context.getDataLoaderRegistry();
    }
}
```

`CustomGraphQLContextBuilder.java` 上下文构建器：

```java
package io.github.llnancy.xuejian.graphql.kickstart.context;

import graphql.kickstart.execution.context.GraphQLContext;
import graphql.kickstart.servlet.context.DefaultGraphQLServletContext;
import graphql.kickstart.servlet.context.GraphQLServletContextBuilder;
import io.github.llnancy.xuejian.graphql.kickstart.context.CustomGraphQLContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;

/**
 * 自定义上下文构建器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/18
 */
@Component
public class CustomGraphQLContextBuilder implements GraphQLServletContextBuilder {

    @Override
    public GraphQLContext build(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String userId = httpServletRequest.getHeader("user_id");
        DefaultGraphQLServletContext context = DefaultGraphQLServletContext.createServletContext()
                .with(httpServletRequest)
                .with(httpServletResponse)
                .build();
        return new CustomGraphQLContext(userId, context);
    }

    @Override
    public GraphQLContext build(Session session, HandshakeRequest handshakeRequest) {
        throw new IllegalStateException("UnSupported");
    }

    @Override
    public GraphQLContext build() {
        throw new IllegalStateException("UnSupported");
    }
}
```

## Instrumentation

`graphql.execution.instrumentation.Instrumentation` 接口提供了很多 `beginXXX` 的方法，这允许我们在 `GraphQL` 请求的各个阶段进行扩展，例如做性能监控和链路追踪等。每个 `beginXXX` 方法被调用时必须返回一个非 `null` 的 `InstrumentationContext` 对象，该对象包含两个回调，一个 `onDispatched` 在被分派时回调，另一个 `onCompleted` 在完成时回调。

以记录请求信息为例，代码示例如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.instrumentation;

import graphql.ExecutionResult;
import graphql.execution.ExecutionId;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 请求日志记录 Instrumentation
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/19
 */
@Component
@Slf4j
public class RequestLoggingInstrumentation extends SimpleInstrumentation {

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(InstrumentationExecutionParameters parameters) {
        LocalDateTime startTime = LocalDateTime.now();
        ExecutionId executionId = parameters.getExecutionInput().getExecutionId();
        log.info("{}: query: {} with variables: {}", executionId, parameters.getQuery(), parameters.getVariables());
        return SimpleInstrumentationContext.whenCompleted((executionResult, throwable) -> {
            Duration duration = Duration.between(startTime, LocalDateTime.now());
            if (throwable == null) {
                log.info("{}: completed successfully in: {}", executionId, duration);
            } else {
                log.error("{}: failed in: {}", executionId, duration, throwable);
            }
        });
    }
}
```

## Tracing 链路追踪

`graphql.execution.instrumentation.tracing.TracingInstrumentation` 类提供了链路追踪的能力，在 `Spring Boot` 中开启链路追踪仅需添加以下配置项：

```yml
graphql:
  servlet:
    tracing-enabled: true
```

以 `playground` 为例，发送请求后可点击右下角 `TRACING` 查看链路信息：

![image-20220519144008840](https://posts-cdn.lilu.org.cn/2022/05/19147cef359a0db7d78112ae5f9402efe02aaff3.png)

## Subscription 发布订阅

`GraphQL` 也提供了类似 `WebSocket` 协议的服务端主动推送能力，基于响应式流。

引入依赖：

```xml
<!-- reactor -->
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <!-- <version>3.4.15</version> -->
</dependency>
```

创建订阅 `graphql/subscription.graphqls` 文件：

```graphql
type Subscription {
    users: User
    user(name: String!): User
}
```

定义了两个订阅：`users` 订阅所有用户，`user` 订阅指定 `name` 的用户。

创建订阅解析器 `UserSubscription.java`，编写代码如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.resolver.user.subscription;

import io.github.llnancy.xuejian.graphql.kickstart.model.User;
import io.github.llnancy.xuejian.graphql.kickstart.publisher.UserPublisher;
import graphql.kickstart.servlet.context.DefaultGraphQLWebSocketContext;
import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

/**
 * subscription 发布订阅
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/19
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UserSubscription implements GraphQLSubscriptionResolver {

    private final UserPublisher publisher;

    public Publisher<User> users(DataFetchingEnvironment environment) {
        DefaultGraphQLWebSocketContext context = environment.getContext();
        return publisher.getUsersPublisher();
    }

    public Publisher<User> user(String name) {
        return publisher.getUserPublisherFor(name);
    }
}
```

其中 `UserPublisher.java` 是基于 `reactor` 响应式流的推送，代码示例如下：

```java
package io.github.llnancy.xuejian.graphql.kickstart.publisher;

import io.github.llnancy.xuejian.graphql.kickstart.model.User;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * user 发布者
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/19
 */
@Component
@Slf4j
public class UserPublisher {

    private final Sinks.Many<User> sink;

    private final Flux<User> flux;

    public UserPublisher() {
        sink = Sinks.many().multicast().directBestEffort();
        flux = sink.asFlux();
    }

    public void publish(User user) {
        sink.tryEmitNext(user);
    }

    public Publisher<User> getUsersPublisher() {
        return flux.map(user -> {
            log.info("Publishing user {}", user);
            return user;
        });
    }

    public Publisher<User> getUserPublisherFor(String name) {
        return flux.filter(user -> name.equals(user.getName()))
                .map(user -> {
                    log.info("Publishing individual subscription for user {}", user);
                    return user;
                });
    }
}
```

接下来在 `createUser` 创建用户的时候要通过 `UserPublisher#publish` 进行事件的发布，修改 `UserMutation#createUser` 方法：

```java
public class UserMutation implements GraphQLMutationResolver {

    private final UserPublisher publisher;

    public User createUser(@Valid CreateUserInput input, DataFetchingEnvironment environment) {
        log.info("Creating user. input: {}", input);
        
        // ......

        User user = User.builder()
                .id(UUID.randomUUID())
                .name(input.getName())
                .sex(input.getSex())
                .age(input.getAge())
                .createdOn(input.getCreatedOn().toLocalDate())
                .createdAt(input.getCreatedAt())
                .address(input.getAddress())
                .build();

        publisher.publish(user);
        return user;
    }
}
```

另外，为了在订阅中通过 `DataFetchingEnvironment` 获取上下文，我们需要在自定义的上下文构建器中构建基于 `WebSocket` 的上下文，代码示例如下：

```java
public class CustomGraphQLContextBuilder implements GraphQLServletContextBuilder {
    
    // ......

    @Override
    public GraphQLContext build(Session session, HandshakeRequest handshakeRequest) {
        return DefaultGraphQLWebSocketContext.createWebSocketContext()
                .with(session)
                .with(handshakeRequest)
                .build();
    }

    // ......
}
```

# 其它

## IDEA 插件

![image-20220519185300116](https://posts-cdn.lilu.org.cn/2022/05/1918c79dc5a3c3d712f969a865506a71cff9d6e4.png)

作用：在 `IDEA` 中写 `graphqls` 文件会有提示，同时文件前面会有 `icon` 图标等。

## voyager

项目地址：[https://github.com/APIs-guru/graphql-voyager](https://github.com/APIs-guru/graphql-voyager)

可以查看 `graphql schema` 之间的关系图。

引入依赖：

```xml
<!--可以查看 graphql 之间的关系图-->
<dependency>
    <groupId>com.graphql-java-kickstart</groupId>
    <artifactId>voyager-spring-boot-starter</artifactId>
    <version>${graphql.starter.version}</version>
    <scope>runtime</scope>
</dependency>
```

默认访问路径为：[http://localhost:8080/voyager](http://localhost:8080/voyager)，界面示例如下：

![image-20220519190359955](https://posts-cdn.lilu.org.cn/2022/05/19193dba605053f3edff0a5e5021eb1343ce1e25.png)

可配置项：

```yml
graphql:
  voyager:
    enabled: true
    basePath: /
    mapping: /voyager
    endpoint: /graphql
    cdn:
      enabled: false
      version: latest
    pageTitle: Voyager
    displayOptions:
      skipRelay: true
      skipDeprecated: true
      rootType: Query
      sortByAlphabet: false
      showLeafFields: true
      hideRoot: false
    hideDocs: false
    hideSettings: false
```
