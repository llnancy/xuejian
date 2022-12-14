- [Spring Boot 整合 RabbitMQ](#spring-boot-整合-rabbitmq)
- [创建工程](#创建工程)
- [基本配置](#基本配置)
- [消息发送](#消息发送)
  - [基本信息配置](#基本信息配置)
  - [主题交换机关系配置](#主题交换机关系配置)
  - [消息投递](#消息投递)
- [消息消费](#消息消费)
- [发送自定义实体消息](#发送自定义实体消息)
  - [`Jackson2JsonMessageConverter`](#jackson2jsonmessageconverter)
- [消息可靠投递](#消息可靠投递)
  - [生产端 Ack](#生产端-ack)
    - [发布正常回调](#发布正常回调)
    - [发布异常回调](#发布异常回调)
    - [设置生产者 ID](#设置生产者-id)
  - [消息回退机制](#消息回退机制)
  - [消费端 Ack](#消费端-ack)
- [死信消息](#死信消息)
  - [原理图](#原理图)
  - [基本信息配置](#基本信息配置-1)
  - [绑定正常交换机和正常队列](#绑定正常交换机和正常队列)
  - [绑定死信交换机和死信队列](#绑定死信交换机和死信队列)
  - [发送带 TTL 过期时间的消息](#发送带-ttl-过期时间的消息)
  - [消费正常队列中的消息](#消费正常队列中的消息)
  - [消费死信队列中的消息](#消费死信队列中的消息)
  - [队列超过长度限制](#队列超过长度限制)
  - [消费者否定应答（消息被拒）](#消费者否定应答消息被拒)
- [延迟消息](#延迟消息)
  - [基于插件的延迟消息](#基于插件的延迟消息)
    - [制作镜像](#制作镜像)
    - [Docker Compose 启动](#docker-compose-启动)
    - [插件基本原理](#插件基本原理)
    - [基本信息配置](#基本信息配置-2)
    - [延迟交换机关系配置](#延迟交换机关系配置)
    - [延迟消息投递](#延迟消息投递)
    - [消费延迟消息](#消费延迟消息)
- [其它类型队列](#其它类型队列)
  - [优先队列](#优先队列)
  - [惰性队列](#惰性队列)
  - [镜像队列](#镜像队列)

# Spring Boot 整合 RabbitMQ

核心依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

# 创建工程

创建 `Spring Boot` 项目 `xuejian-rabbitmq`，引入 `amqp` 及一些必要的 `web` 依赖，完整 `pom.xml` 文件如下：

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

    <artifactId>xuejian-rabbitmq</artifactId>
    <description>Spring Boot 整合 RabbitMQ 消息队列</description>

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
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>
</project>
```

# 基本配置

创建 `Spring Boot` 配置文件 `application.yml`，添加以下基本配置项：

```yml
spring:
  rabbitmq:
    host: 127.0.0.1 # host
    port: 5672 # 端口号，默认值5672。
    username: guest # 用户名，默认值guest。
    password: guest # 密码，默认值guest。
    virtual-host: / # 虚拟主机，默认值/。
```

# 消息发送

这里以 `topic` 主题类型交换机为例进行消息发送。

> `topic` 主题类型交换机可通过设置特殊的绑定键实现 `fanout` 扇出类型交换机和 `direct` 直接类型交换机的效果。
>
> - `fanout` 扇出类型交换机：绑定键为 `#`。
> - `direct` 直接类型交换机：绑定键中不包含 `*` 和 `#`。

## 基本信息配置

使用配置文件配置交换机名称、队列名称及绑定键名称。

创建 `RabbitMQ` 属性配置信息类 `RabbitMQProperties`，编写代码如下：

```java
package io.github.llnancy.xuejian.rabbitmq.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 属性配置信息类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Configuration
@ConfigurationProperties(prefix = "my.rabbitmq")
@Data
public class RabbitMQProperties {

    /**
     * 交换机名称
     */
    private String topicExchangeName;

    /**
     * 队列名称
     */
    private String topicQueueName;

    /**
     * 绑定键
     */
    private String topicRoutingKey;
}
```

然后在 `application.yml` 中进行自定义配置，例如：

```yml
# 自定义的交换机、队列及绑定键的名称
my:
  rabbitmq:
    topic-exchange-name: boot_topic
    topic-queue-name: boot_topic_queue
    topic-routing-key: boot.#
```

## 主题交换机关系配置

配置交换机、队列及它们之间的绑定关系。

创建 `RabbitMQ` 交换机关系配置类 `BootTopicConfig`，编写代码如下：

```java
package io.github.llnancy.xuejian.rabbitmq.config;

import io.github.llnancy.xuejian.rabbitmq.config.property.RabbitMQProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 交换机、队列、binding关系 配置类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Configuration
public class BootTopicConfig {

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    /**
     * 创建主题交换机。
     * ExchangeBuilder中默认durable=true，持久化。
     *
     * @return TopicExchange
     * @see org.springframework.amqp.core.TopicExchange
     */
    @Bean
    public Exchange bootTopicExchange() {
        return ExchangeBuilder.topicExchange(rabbitMQProperties.getTopicExchangeName())
                .build();
    }

    /**
     * 创建队列。
     * durable方法创建的是持久化队列
     *
     * @return Queue
     */
    @Bean
    public Queue bootQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getTopicQueueName())
                .build();
    }

    /**
     * 交换机和队列进行绑定
     *
     * @return Binding
     */
    @Bean
    public Binding bindingBootQueueExchange() {
        return BindingBuilder.bind(bootQueue())
                .to(bootTopicExchange())
                .with(rabbitMQProperties.getTopicRoutingKey())
                .noargs();
    }
}
```

## 消息投递

调用 `RabbitTemplate.convertAndSend` 方法将消息用指定的 `routingKey`（路由键）发送到指定交换机 `boot_topic`。

创建消息生产者类 `RabbitMQProducer`，编写代码如下：

```java
package io.github.llnancy.xuejian.rabbitmq.mq.producer;

import io.github.llnancy.xuejian.rabbitmq.config.property.RabbitMQProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * topic 交换机 消息生产者
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Component
public class RabbitMQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    public void send(String msg, String routingKey) {
        rabbitTemplate.convertAndSend(
                rabbitMQProperties.getTopicExchangeName(),
                routingKey,
                msg
        );
    }
}
```

为了方便触发消息发送，我们暴露出一个 `HTTP` 接口。创建控制器类 `RabbitMQController`，代码如下：

```java
package io.github.llnancy.xuejian.rabbitmq.controller;

import io.github.llnancy.xuejian.rabbitmq.mq.producer.RabbitMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息发送入口
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@RestController
public class RabbitMQController {

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @GetMapping("/send")
    public void send(String msg, String routingKey) {
        rabbitMQProducer.send(msg, routingKey);
    }
}
```

启动服务后访问 [http://localhost:8080/send?msg=xxx&routingKey=boot.xxx](http://localhost:8080/send?msg=xxx&routingKey=boot.xxx) 即可发送消息。

# 消息消费

使用 `@RabbitListener` 和 `@RabbitHandler` 注解进行消息监听与处理。

创建消息消费者类 `RabbitMQConsumer`，编写代码如下：

```java
package io.github.llnancy.xuejian.rabbitmq.mq.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消息消费者
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Component
@RabbitListener(queues = "${my.rabbitmq.topic-queue-name}")
@Slf4j
public class RabbitMQConsumer {

    @RabbitHandler
    public void listener(String msg, Message message, Channel channel) {
        log.info("RabbitMQ - [RabbitMQConsumer] 消费端 消费到消息，msg={}, message={}", msg, message);
    }
}
```

`@RabbitHandler` 注解标记的 `listener` 方法的第一个参数 `msg` 为生产者发送的消息对象，类型为 `String`；第二个参数 `message` 为标准 `AMQP` 协议的 `Message` 对象；第三个参数 `channel` 是消息发送的信道。

启动服务后访问 [http://localhost:8080/send?msg=xxx&routingKey=boot.xxx](http://localhost:8080/send?msg=xxx&routingKey=boot.xxx) 发送消息，消费者接收到消息后将消息内容打印在控制台：

```shell
2022-04-26 17:17:01.315  INFO 20092 --- [ntContainer#0-1] i.g.l.x.m.c.RabbitMQConsumer       : RabbitMQ - [RabbitMQConsumer] 消费端 消费到消息，msg=xxx, message=(Body:'xxx' MessageProperties [headers={}, contentType=text/plain, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=boot_topic, receivedRoutingKey=boot.xxx, deliveryTag=1, consumerTag=amq.ctag-J6pgj9XCNuV9LeCRkY825g, consumerQueue=boot_topic_queue])
```

至此，我们就完成了 `Spring Boot` 和 `RabbitMQ` 的整合，可以对消息进行简单的发送与接收。

# 发送自定义实体消息

实际业务中，不可能仅发送 `String` 类型的消息。如果不是直接将消息转化为 `byte` 数组并设置到 `org.springframework.amqp.core.Message` 类中进行发送，`Spring Boot AMQP` 会使用 `MessageConverter` 消息格式转化器在消息发送和接收时自动将消息内容序列化为 `byte` 数组或将 `byte` 数组反序列化为消息，默认使用的转化器是 `SimpleMessageConverter`，前面我们发送的 `String` 类型的消息能被正常接收就是有转化器的存在。它支持以下三种格式的消息：

1. `byte` 数组
2. `String`
3. 实现了 `java.io.Serializable` 接口的对象。

例如现在我们想将一个业务 `DTO` 对象作为消息进行发送，默认情况下需要 `DTO` 类实现 `Serializable` 接口。示例如下：

```java
package io.github.llnancy.xuejian.rabbitmq.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 自定义实体消息
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/26
 */
@Data
public class MsgDTO implements Serializable {

    private static final long serialVersionUID = -51410032238146012L;

    private String msg;
}
```

然后我们就可以将 `MsgDTO` 类的对象作为消息进行发送了。消息生产者代码如下：

```java
public void send(MsgDTO msgDTO, String routingKey) {
    rabbitTemplate.convertAndSend(
        rabbitMQProperties.getTopicExchangeName(),
        routingKey,
        msgDTO
    );
}
```

同样地，我们暴露一个 `HTTP` 接口便于触发消息发送。代码如下：

```java
@PostMapping("/send")
public void send(@RequestBody MsgDTO msgDTO, String routingKey) {
    rabbitMQProducer.send(msgDTO, routingKey);
}
```

接下来在消费端 `RabbitMQConsumer` 类中，我们可以提供一个重载的 `listener` 方法进行消费。代码示例如下：

```java
@RabbitHandler
public void listener(MsgDTO msg, Message message, Channel channel) {
    log.info("RabbitMQ - [RabbitMQConsumer] 消费端 消费到消息，msg={}, message={}", msg, message);
}
```

重启服务后使用命令 `curl --location --request POST 'localhost:8080/send?routingKey=boot.xxx' --header 'Content-Type: application/json' --data-raw '{"msg": "xxx"}'` 发送消息。控制台输出如下：

```shell
2022-04-26 17:01:11.244  INFO 22279 --- [ntContainer#0-1] i.g.l.x.r.m.c.RabbitMQConsumer       : RabbitMQ - [RabbitMQConsumer] 消费端 消费到消息，msg=MsgDTO(msg=xxx), message=(Body:'{"msg":"xxx"}' MessageProperties [headers={__TypeId__=model.io.github.llnancy.xuejian.rabbitmq.MsgDTO}, contentType=application/json, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=boot_topic, receivedRoutingKey=boot.xxx, deliveryTag=1, consumerTag=amq.ctag-MpLMZE0hHfGnLzFrc_XoCQ, consumerQueue=boot_topic_queue])
```

## `Jackson2JsonMessageConverter`

`SimpleMessageConverter` 消息格式转换器序列化自定义实体类时使用的是 `JDK` 的序列化方式，性能略差，且不支持跨语言发送消息，不推荐使用。实际生产环境中通常会使用 `Jackson2JsonMessageConverter` 转化器，它基于 `JSON` 格式，底层用的是 `Jackson`。

`Spring Boot` 配置 `Jackson2JsonMessageConverter` 转化器的方式如下：

```java
package io.github.llnancy.xuejian.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * RabbitMQ 基本配置类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Configuration
@Slf4j
public class RabbitMQConfig {

    /**
     * 消息内容使用 JSON 序列化和反序列化
     * 消息生产端 RabbitTemplate 会通过 RabbitTemplateConfigurer 自动注入该 Bean
     * 消息消费端 SimpleRabbitListenerContainerFactory 会自动注入该 Bean
     *
     * @return Jackson2JsonMessageConverter
     * @see org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
     * @see org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer
     * @see org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
```

# 消息可靠投递

实际生产环境中，我们经常需要考虑的一个问题是：如何保证消息不丢失？

对于 `RabbitMQ` 来说，可以从以下三个方面进行考虑：

- 生产端 `Ack` 机制。消息生产者将消息投递至交换机时进行发布确认。
- 消息回退机制。交换机路由消息至队列时，如果找不到合适的队列进行投递，则将消息回退给消息发起者。
- 消费端 `Ack` 机制。消息消费者可以开启手动应答，当消息的处理逻辑执行成功后，由消费者主动发起肯定应答；如果消息处理过程中发生异常，则发起否定应答。如果应答过程中消费者线程挂掉无法进行应答，`RabbitMQ` 还提供了应答超时时间进行兜底处理。

## 生产端 Ack

配置 `spring.rabbitmq.publisher-confirm-type` 参数开启生产者的发布确认。共三个取值：

- `NONE`：默认值。禁用发布确认。
- `CORRELATED`：异步发布确认。触发 `ConfirmCallback` 回调。建议使用。
- `SIMPLE`：同步发布确认。发送消息成功后主动调用 `RabbitTemplate#waitForConfirms` 或 `RabbitTemplate#waitForConfirmsOrDie` 方法等待确认结果。需要保证发送消息和等待确认这两步操作在同一个作用域内，可以使用 `RabbitTemplate.invoke` 方法实现。同步确认效率较低，不推荐使用。

示例配置如下：

```yml
spring:
  rabbitmq:
    publisher-confirm-type: correlated # 开启生产者发布确认：异步模式。会触发 ConfirmCallback 回调。
```

创建回调类 `RabbitMQCallback`，实现 `RabbitTemplate.ConfirmCallback` 回调接口重写 `confirm` 方法，同时注入 `RabbitTemplate` 并为其配置 `ConfirmCallback` 回调。完整代码如下：

```java
package io.github.llnancy.xuejian.rabbitmq.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * RabbitMQ 消息可靠投递 生产者发布确认回调
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Configuration
@Slf4j
public class RabbitMQCallback implements InitializingBean, RabbitTemplate.ConfirmCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 生产者发布确认回调
     * 需要开启 spring.rabbitmq.publisher-confirm-type 参数，建议设置为 correlated，异步回调。
     *
     * @param correlationData 可保存消息ID和消息内容，需要发送方在消息发送时自行设置
     * @param ack             true：交换机收到消息；false：交换机未收到消息
     * @param cause           投递到交换机失败的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = Objects.isNull(correlationData) ? "" : correlationData.getId();
        if (ack) {
            log.info("RabbitMQ - [ConfirmCallback] 生产端ID={}的消息投递至交换机 成功", id);
        } else {
            log.error("RabbitMQ - [ConfirmCallback] 生产端ID={}的消息投递至交换机 失败，原因：{}", id, cause);
        }
    }
}
```

### 发布正常回调

重启服务后通过 [http://localhost:8080/send?msg=xxx&routingKey=boot.xxx](http://localhost:8080/send?msg=xxx&routingKey=boot.xxx) 接口发送一条消息，消息正常投递到了交换机，回调 `ack=true`。控制台输出如下：

```shell
2022-04-26 17:18:51.644  INFO 20147 --- [nectionFactory1] i.g.l.x.rabbitmq.config.RabbitMQCallback   : RabbitMQ - [ConfirmCallback] 生产端ID=的消息投递至交换机 成功
```

### 发布异常回调

修改消息发送者的代码，将消息发送给一个不存在的交换机，模拟发布异常。代码示例如下：

```java
public void send(String msg, String routingKey) {
    rabbitTemplate.convertAndSend(
        rabbitMQProperties.getTopicExchangeName() + "xxx",
        routingKey,
        msg
    );
}
```

重启服务后通过 [http://localhost:8080/send?msg=xxx&routingKey=boot.xxx](http://localhost:8080/send?msg=xxx&routingKey=boot.xxx) 接口发送一条消息，这时生产者会找不到指定交换机，消息投递失败，回调 `ack=false`。控制台输出如下：

```shell
2022-04-26 17:19:23.544 ERROR 20166 --- [ 127.0.0.1:5672] o.s.a.r.c.CachingConnectionFactory       : Shutdown Signal: channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'boot_topicxxx' in vhost '/', class-id=60, method-id=40)
2022-04-26 17:19:23.548 ERROR 20166 --- [nectionFactory2] i.g.l.x.rabbitmq.config.RabbitMQCallback   : RabbitMQ - [ConfirmCallback] 生产端ID=的消息投递至交换机 失败，原因：channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'boot_topicxxx' in vhost '/', class-id=60, method-id=40)
```

### 设置生产者 ID

注意到控制台输出的生产端 `ID` 一直为空，该 `ID` 为 `CorrelationData` 类的 `id` 字段，它是由发送方在消息发送时自行设置的。`CorrelationData` 类还可存储原消息内容，可用于消息投递给交换机失败回调时进行重试处理。

`RabbitTemplate` 有一个包含四个参数的 `convertAndSend` 重载的方法可用于设置 `CorrelationData`。示例用法如下：

```java
public void send(String msg, String routingKey) {
    rabbitTemplate.convertAndSend(
        rabbitMQProperties.getTopicExchangeName(),
        routingKey,
        msg,
        new CorrelationData()
    );
}
```

`CorrelationData` 类的无参构造函数将 `ID` 设置为了 `UUID`。重启服务后通过 [http://localhost:8080/send?msg=xxx&routingKey=boot.xxx](http://localhost:8080/send?msg=xxx&routingKey=boot.xxx) 接口发送一条消息，控制台输出如下：

```shell
2022-04-26 17:20:30.678  INFO 20203 --- [nectionFactory1] i.g.l.x.rabbitmq.config.RabbitMQCallback   : RabbitMQ - [ConfirmCallback] 生产端ID=b643ed26-a28c-43a1-9b12-af7aed3c18d5的消息投递至交换机 成功
```

## 消息回退机制

配置 `spring.rabbitmq.publisher-returns=true` 开启消息回退机制。当交换机找不到合适的队列发送消息时，触发 `ReturnsCallback` 回调，需要配合 `spring.rabbitmq.template.mandatory=true` 使用。

示例配置如下：

```yml
spring:
  rabbitmq:
    publisher-returns: true # 开启消息回退，消息路由不到合适的队列时，触发 ReturnsCallback 回调，配合 RabbitTemplate 的 mandatory=true 使用。
    template:
      mandatory: true # true：消息路由不到合适的队列时，强制触发 ReturnsCallback 回调。
```

让回调类 `RabbitMQCallback` 实现 `RabbitTemplate.ReturnsCallback` 回调接口重写 `returnedMessage` 方法，然后给 `RabbitTemplate` 配置 `ReturnsCallback` 回调。

`RabbitMQCallback` 类完整代码如下：

```java
package io.github.llnancy.xuejian.rabbitmq.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * RabbitMQ 消息可靠投递 生产者发布确认回调和消息回退回调
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Configuration
@Slf4j
public class RabbitMQCallback implements InitializingBean, RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    /**
     * 生产者发布确认回调
     * 需要开启 spring.rabbitmq.publisher-confirm-type 参数，建议设置为 correlated，异步回调。
     *
     * @param correlationData 可保存消息ID和消息内容，需要发送方在消息发送时自行设置
     * @param ack             true：交换机收到消息；false：交换机未收到消息
     * @param cause           投递到交换机失败的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = Objects.isNull(correlationData) ? "" : correlationData.getId();
        if (ack) {
            log.info("RabbitMQ - [ConfirmCallback] 生产端ID={}的消息投递至交换机 成功", id);
        } else {
            log.error("RabbitMQ - [ConfirmCallback] 生产端ID={}的消息投递至交换机 失败，原因：{}", id, cause);
        }
    }

    /**
     * 交换机路由消息至队列失败 - 消息回退回调
     * 需要开启 spring.rabbitmq.publisher-returns 参数，设置为 true。
     * 同时需要配合 spring.rabbitmq.template.mandatory 参数使用，也设置为 true。
     *
     * @param returned 退回的消息
     */
    @Override
    public void returnedMessage(@NonNull ReturnedMessage returned) {
        log.error("RabbitMQ - [ReturnsCallback] 交换机路由消息至队列失败，消息退回发起者，消息内容为：{}", returned);
    }
}
```

重启服务后通过 [http://localhost:8080/send?msg=xxx&routingKey=xxxboot.xxx](http://localhost:8080/send?msg=xxx&routingKey=xxxboot.xxx) 接口发送消息，修改请求参数 `routingKey` 路由键为 `xxxboot.xxx`，模拟交换机路由消息至队列失败。此时的情况是生产端投递消息给交换机成功，交换机路由消息至队列失败，触发 `ReturnsCallback` 回调。控制台输出如下：

```shell
2022-04-26 17:21:49.365 ERROR 20225 --- [nectionFactory1] i.g.l.x.rabbitmq.config.RabbitMQCallback   : RabbitMQ - [ReturnsCallback] 交换机路由消息至队列失败，消息退回发起者，消息内容为：ReturnedMessage [message=(Body:'xxx' MessageProperties [headers={spring_returned_message_correlation=66b1b1a1-8d16-4f1e-a615-980cc6dd299b}, contentType=text/plain, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, deliveryTag=0]), replyCode=312, replyText=NO_ROUTE, exchange=boot_topic, routingKey=xxxboot.xxx]
2022-04-26 17:21:49.366  INFO 20225 --- [nectionFactory2] i.g.l.x.rabbitmq.config.RabbitMQCallback   : RabbitMQ - [ConfirmCallback] 生产端ID=66b1b1a1-8d16-4f1e-a615-980cc6dd299b的消息投递至交换机 成功
```

## 消费端 Ack

配置 `spring.rabbitmq.listener.simple.acknowledge-mode=manual` 开启消费端手动 `ack` 应答。示例配置如下：

```yml
spring:
  rabbitmq:
    listener:
      # 容器类型：默认值为 simple，即 SimpleMessageListenerContainer。
      type: simple
      simple:
        acknowledge-mode: manual # manual：开启消费端手动 ack 应答。默认值为 auto 自动应答。
```

改造消费者 `RabbitMQConsumer` 的 `listener` 方法，使用 `try-catch` 语法包裹消息处理部分，并在处理完成后调用 `channel.basicAck` 方法进行肯定应答，在 `catch` 代码块中调用 `channel.basicNack` 方法进行否定应答。

`channel.basicAck` 方法有两个参数：

- `deliveryTag`：消息传递标记。可通过调用 `message.getMessageProperties().getDeliveryTag()` 获取。
- `multiple`：是否批量应答。设置为 `true` 时会应答通道内该消息之前的所有未应答消息。建议设置为 `false` 不批量。

`channel.basicNack` 方法在 `basicAck` 方法基础上新增一个参数 `requeue`：否定应答后是否将消息重新投递至队列。可用于消费异常后将消息重新投递至队列进行重试。

完整代码如下：

```java
package io.github.llnancy.xuejian.rabbitmq.mq.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 消息消费者
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Component
@RabbitListener(queues = "${my.rabbitmq.topic-queue-name}")
@Slf4j
public class RabbitMQConsumer {

    @RabbitHandler
    public void listener(String msg, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("RabbitMQ - [RabbitMQConsumer] 消费端 消费到消息，msg={}, message={}", msg, message);
            if ("error".equals(msg)) {
                throw new RuntimeException("error");
            }
            // 手动ack，肯定应答，multiple=false表示不批量
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("RabbitMQ - [RabbitMQConsumer] 消费端 消费消息时发生异常", e);
            try {
                // 否定应答，multiple=false表示不批量，requeue=false表示不重新投递至原队列
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ex) {
                log.error("RabbitMQ - [RabbitMQConsumer] 消费端 否定应答消息时发生异常", ex);
            }
        }
    }
}
```

当投递的消息内容为 `error` 时会抛出异常进行否定应答。重启服务后分别访问 [http://localhost:8080/send?msg=xxx&routingKey=boot.xxx](http://localhost:8080/send?msg=xxx&routingKey=boot.xxx) 发送正常消息和 [http://localhost:8080/send?msg=error&routingKey=boot.xxx](http://localhost:8080/send?msg=error&routingKey=boot.xxx) 发送”异常“消息。

正常消息控制台输出如下：

```shell
2022-04-26 18:07:32.538  INFO 20521 --- [ntContainer#0-1] i.g.l.x.r.m.c.RabbitMQConsumer       : RabbitMQ - [RabbitMQConsumer] 消费端 消费到消息，msg=xxx, message=(Body:'xxx' MessageProperties [headers={spring_listener_return_correlation=31a0cc2c-9551-442e-bbdb-99ce744d1357, spring_returned_message_correlation=04bc3584-c62b-4afa-ba09-3d13393dc7d9}, contentType=text/plain, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=boot_topic, receivedRoutingKey=boot.xxx, deliveryTag=3, consumerTag=amq.ctag-4hgZDTG0hVmjOA3nltqx0A, consumerQueue=boot_topic_queue])
2022-04-26 18:07:32.544  INFO 20521 --- [nectionFactory2] i.g.l.x.rabbitmq.config.RabbitMQCallback   : RabbitMQ - [ConfirmCallback] 生产端ID=04bc3584-c62b-4afa-ba09-3d13393dc7d9的消息投递至交换机 成功
```

异常消息控制台输出如下：

```shell
2022-04-26 18:09:46.099  INFO 21303 --- [nectionFactory1] i.g.l.x.rabbitmq.config.RabbitMQCallback   : RabbitMQ - [ConfirmCallback] 生产端ID=7d88f590-b237-48b1-84b0-e9467dd3f969的消息投递至交换机 成功
2022-04-26 18:09:46.106  INFO 21303 --- [ntContainer#0-1] i.g.l.x.r.m.c.RabbitMQConsumer       : RabbitMQ - [RabbitMQConsumer] 消费端 消费到消息，msg=error, message=(Body:'error' MessageProperties [headers={spring_listener_return_correlation=1cafb3d1-ceca-4c6d-9bf7-be1fe7955506, spring_returned_message_correlation=7d88f590-b237-48b1-84b0-e9467dd3f969}, contentType=text/plain, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=boot_topic, receivedRoutingKey=boot.xxx, deliveryTag=1, consumerTag=amq.ctag-0u75YXhiAWNFddHyktk44w, consumerQueue=boot_topic_queue])
2022-04-26 18:09:46.110 ERROR 21303 --- [ntContainer#0-1] i.g.l.x.r.m.c.RabbitMQConsumer       : RabbitMQ - [RabbitMQConsumer] 消费端 消费消息时发生异常

java.lang.RuntimeException: error
	at io.github.llnancy.xuejian.rabbitmq.mq.consumer.RabbitMQConsumer.listener(RabbitMQConsumer.java:29) ~[classes/:na]
	...
```

# 死信消息

在某些特殊情况下，正常队列中的消息可能会无法被消费到，这样的消息称为死信。通常，死信有以下三个来源：

1. 消息到达 `TTL` 过期时间。
2. 队列超过了长度限制。
3. 消费者使用 `basic.nack` 或 `basic.reject` 否定应答消息，并将 `requeue` 参数设置为 `false`。

对于死信，我们可以为其配置一个特殊的交换机和队列，将死信转发到特殊交换机和队列上进行后续处理。

## 原理图

![原理图](https://posts-cdn.lilu.org.cn/2022/04/2719935374520cce15d170d81ac96969c59a52d2.png)

核心是在创建正常队列时为其配置死信交换机，同时可选择配置死信队列的绑定键。当正常队列中出现死信时，就会自动将其转发到死信交换机，然后路由到死信队列进行消费。

## 基本信息配置

在属性配置信息类 `RabbitMQProperties` 中添加正常交换机和死信交换机等字段，代码如下：

```java
package io.github.llnancy.xuejian.rabbitmq.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 属性配置信息类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Configuration
@ConfigurationProperties(prefix = "my.rabbitmq")
@Data
public class RabbitMQProperties {
    
    // ...省略前文主题交换机及队列等字段

    /**
     * 正常交换机名称
     */
    private String normalExchangeName;

    /**
     * 正常队列名称
     */
    private String normalQueueName;

    /**
     * 正常队列绑定键
     */
    private String normalRoutingKey;

    /**
     * 死信交换机名称
     */
    private String deadLetterExchangeName;

    /**
     * 死信队列名称
     */
    private String deadLetterQueueName;

    /**
     * 死信队列绑定键
     */
    private String deadLetterRoutingKey;
}
```

然后在`application.yml`文件中进行配置。示例如下：

```yml
my:
  rabbitmq:
    normal-exchange-name: normal_exchange
    normal-queue-name: normal_queue
    normal-routing-key: normal.#
    dead-letter-exchange-name: dead_exchange
    dead-letter-queue-name: dead_queue
    dead-letter-routing-key: dead.#
```

## 绑定正常交换机和正常队列

将正常收发消息的交换机和队列进行绑定，同时配置死信交换机。代码示例如下：

```java
package io.github.llnancy.xuejian.rabbitmq.config;

import property.config.io.github.llnancy.xuejian.rabbitmq.RabbitMQProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 死信队列 - 正常收发消息的交换机与队列关系配置
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/27
 */
@Configuration
public class NormalConfig {

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    /**
     * 正常交换机。
     *
     * @return TopicExchange
     */
    @Bean
    public Exchange normalExchange() {
        return ExchangeBuilder.topicExchange(rabbitMQProperties.getNormalExchangeName())
                .build();
    }

    /**
     * 创建队列。
     * durable 方法创建的是持久化队列
     * deadLetterExchange 方法设置死信交换机
     * deadLetterRoutingKey 方法设置死信队列绑定键
     *
     * @return Queue
     */
    @Bean
    public Queue normalQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getNormalQueueName())
                .deadLetterExchange(rabbitMQProperties.getDeadLetterExchangeName())
                .deadLetterRoutingKey(rabbitMQProperties.getDeadLetterRoutingKey())
                .build();
    }

    /**
     * 交换机和队列进行绑定
     *
     * @return Binding
     */
    @Bean
    public Binding bindingNormalQueueExchange() {
        return BindingBuilder.bind(normalQueue())
                .to(normalExchange())
                .with(rabbitMQProperties.getNormalRoutingKey())
                .noargs();
    }
}
```

调用 `deadLetterExchange` 方法会给队列配置 `x-dead-letter-exchange` 参数表示设置死信交换机，调用 `deadLetterRoutingKey` 方法会给队列配置 `x-dead-letter-routing-key` 参数表示设置死信交换机和队列的 `routingKey` 绑定键。

## 绑定死信交换机和死信队列

将死信交换机和队列进行绑定。代码示例如下：

```java
package io.github.llnancy.xuejian.rabbitmq.config;

import property.config.io.github.llnancy.xuejian.rabbitmq.RabbitMQProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 死信交换机与队列配置
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/27
 */
@Configuration
public class DeadLetterConfig {

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    /**
     * 死信交换机
     *
     * @return TopicExchange
     */
    @Bean
    public Exchange deadLetterExchange() {
        return ExchangeBuilder.topicExchange(rabbitMQProperties.getDeadLetterExchangeName())
                .build();
    }

    /**
     * 死信队列
     *
     * @return Queue
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getDeadLetterQueueName())
                .build();
    }

    /**
     * 死信交换机和死信队列进行绑定
     *
     * @return Binding
     */
    @Bean
    public Binding bindingDeadQueueExchange() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(rabbitMQProperties.getDeadLetterRoutingKey())
                .noargs();
    }
}
```

## 发送带 TTL 过期时间的消息

让消息具有 `TTL` 过期时间有两种方式：

1. 通过队列属性 `x-message-ttl` 进行设置。设置后队列中所有消息都具有相同的过期时间。
2. 由发送方为每条消息单独设置 `TTL` 时间。更加灵活，推荐使用。

> 注意：如果两种方式同时使用，则以 `TTL` 时间设置的较短的为准。

`RabbitTemplate` 有一个 `convertAndSend` 重载方法的参数中包含 `MessagePostProcessor` 类，可以通过 `MessagePostProcessor` 类为消息设置 `TTL` 过期时间。示例用法如下：

```java
public void send(MsgDTO msgDTO, String routingKey, String expire) {
    rabbitTemplate.convertAndSend(
        rabbitMQProperties.getNormalExchangeName(),
        routingKey,
        msgDTO,
        message -> {
            MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setExpiration(expire);// 消息过期时间，单位ms。
            return message;
        },
        new CorrelationData()
    );
}
```

同样地，我们暴露一个 `HTTP` 接口便于触发 `TTL` 消息的发送。代码如下：

```java
@PostMapping("/send-ttl")
public void send(@RequestBody MsgDTO msgDTO, String routingKey, String expire) {
    rabbitMQProducer.send(msgDTO, routingKey, expire);
}
```

重启服务后使用命令 `curl --location --request POST 'localhost:8080/send-ttl?routingKey=normal.xxx&expire=10000' --header 'Content-Type: application/json' --data-raw '{"msg": "xxx"}'` 往正常队列中发送一条 `TTL=10s` 的消息。

如果消息到达 `TTL` 时间后还未被消费，则会被转发到配置好的死信队列中，可在控制台页面进行查看。

![281390783a0ec5be907fc7920b202715c989c2b0](https://posts-cdn.lilu.org.cn/2022/04/281390783a0ec5be907fc7920b202715c989c2b0.png)

`normal_queue` 队列中的消息数量 `Ready=Total=1`，将在 `TTL=10s` 到期后被转发至 `dead_queue` 死信队列。

![28147199bd15d11c16fb6a46ccfc8eaa58d4da86](https://posts-cdn.lilu.org.cn/2022/04/28147199bd15d11c16fb6a46ccfc8eaa58d4da86.png)

## 消费正常队列中的消息

消息体为 `MsgDTO` 自定义实体对象，如果与前文的消费逻辑一致，直接在 `@RabbitListener` 注解上添加队列名 `my.rabbitmq.normal-queue-name` 即可，示例如下：

```java
@RabbitListener(queues = {"${my.rabbitmq.topic-queue-name}", "${my.rabbitmq.normal-queue-name}"})
```

这样两个队列中类型为 `MsgDTO` 的消息的消费逻辑就都是以下方法：

```java
@RabbitHandler
public void listener(MsgDTO msg, Message message, Channel channel) {...}
```

## 消费死信队列中的消息

在 `@RabbitListener` 注解上添加死信队列名 `my.rabbitmq.dead-letter-queue-name`。代码示例如下：

```java
@RabbitListener(queues = {
        "${my.rabbitmq.topic-queue-name}",
        "${my.rabbitmq.normal-queue-name}",
        "${my.rabbitmq.dead-letter-queue-name}"
})
```

这样，死信队列中类型为 `MsgDTO` 的消息的消费逻辑也为：

```java
@RabbitHandler
public void listener(MsgDTO msg, Message message, Channel channel) {...}
```

> 如果死信队列中的消息需要进行特殊告警等处理，可以进行单独的消费封装。这里是为了复用消费逻辑。

## 队列超过长度限制

给正常队列配置队列长度。代码示例如下：

```java
/**
 * 创建队列。
 * durable 方法创建的是持久化队列
 * deadLetterExchange 方法设置死信交换机
 * deadLetterRoutingKey 方法设置死信队列绑定键
 * maxLength 方法设置队列长度
 *
 * @return Queue
 */
@Bean
public Queue normalQueue() {
    return QueueBuilder.durable(rabbitMQProperties.getNormalQueueName())
        .deadLetterExchange(rabbitMQProperties.getDeadLetterExchangeName())
        .deadLetterRoutingKey(rabbitMQProperties.getDeadLetterRoutingKey())
        .maxLength(6)
        .build();
}
```

给队列添加 `x-max-length` 参数以限制队列最大长度。这里限制最大长度为 `6`。由于队列一旦创建后便无法修改其定义，所以我们需要去控制台手动删除队列，然后为了看到效果，我们先不要消费普通队列和死信队列中的消息，注释掉 `@RabbitListener` 注解中的 `${my.rabbitmq.normal-queue-name}` 和 `${my.rabbitmq.dead-letter-queue-name}`。

生产者代码如下：

```java
public void sendNormalExchange(MsgDTO msgDTO, String routingKey) {
    rabbitTemplate.convertAndSend(
            rabbitMQProperties.getNormalExchangeName(),
            routingKey,
            msgDTO,
            new CorrelationData()
    );
}
```

另外，我们还要暴露一个 `HTTP` 接口便于我们往正常队列中发送消息，控制器 `MQController` 代码如下：

```java
@PostMapping("/send-normal")
public void sendNormalExchange(@RequestBody MsgDTO msgDTO, String routingKey) {
    rabbitMQProducer.sendNormalExchange(msgDTO, routingKey);
}
```

重启服务后使用命令

```shell
for i in {1..10}
do
    curl --location --request POST 'localhost:8080/send-normal?routingKey=normal.xxx' --header 'Content-Type: application/json' --data-raw '{"msg": "xxx"}'
done
```

一次性发送 `10` 条消息，观察控制台。

![2815d47ed58e8fd020ce39100b6c9e6b5d3b7e3d](https://posts-cdn.lilu.org.cn/2022/04/2815d47ed58e8fd020ce39100b6c9e6b5d3b7e3d.png)

可看到 `normal_queue` 队列中存在 `6` 条消息，而 `dead_queue` 死信队列中存在 `4` 条。（测试完成后打开消费者的注释重启服务即可消费掉消息）

## 消费者否定应答（消息被拒）

修改 `MsgDTO` 类型消息消费者代码，当 `MsgDTO` 对象中的 `msg` 属性值为 `error` 时抛出异常，然后拒绝消息。代码示例如下：

```java
@RabbitHandler
public void listener(MsgDTO msg, Message message, Channel channel) {
    long deliveryTag = message.getMessageProperties().getDeliveryTag();
    try {
        log.info("RabbitMQ - [RabbitMQConsumer] 消费端 消费到消息，msg={}, message={}", msg, message);
        if ("error".equals(msg.getMsg())) {
            throw new RuntimeException("error");
        }
        // 手动ack，肯定应答，multiple=false表示不批量
        channel.basicAck(deliveryTag, false);
    } catch (Exception e) {
        log.error("RabbitMQ - [RabbitMQConsumer] 消费端 消费消息时发生异常", e);
        try {
            // 否定应答，multiple=false表示不批量，requeue=false表示不重新投递至原队列，如果配置了死信交换机则会自动转发
            channel.basicNack(deliveryTag, false, false);
        } catch (IOException ex) {
            log.error("RabbitMQ - [RabbitMQConsumer] 消费端 否定应答消息时发生异常", ex);
        }
    }
}
```

打开对 `${my.rabbitmq.normal-queue-name}` 正常队列的消费，同时暂时注释掉对 `${my.rabbitmq.dead-letter-queue-name}` 死信队列的消费。重启服务后是用以下命令

```shell
for i in {1..5}
do
    if [ "$i" -eq "2" ]; then
        curl --location --request POST 'localhost:8080/send-normal?routingKey=normal.xxx' --header 'Content-Type: application/json' --data-raw '{"msg": "error"}'
    else
        curl --location --request POST 'localhost:8080/send-normal?routingKey=normal.xxx' --header 'Content-Type: application/json' --data-raw '{"msg": "xxx"}'
    fi
done
```

往正常队列中发送 `5` 条消息，其中第二条消息的 `msg` 字段为 `error`，即该消息会被否定应答，进入死信队列。查看 `IDEA` 控制台可看到正常消费了四条消息，剩下一条消息消费失败抛出了异常；查看 `RabbitMQ` 控制台可看到死信队列中存在一条消息。

![281773515008fb18f2d090b25e1c3306d4963047](https://posts-cdn.lilu.org.cn/2022/04/281773515008fb18f2d090b25e1c3306d4963047.png)

# 延迟消息

实现方式之一是给队列设置过期时间，基于死信来实现，和[死信原理图](#原理图)一样，需要配置两个交换机和队列，给正常队列设置延迟时间但不进行消费，消费者仅监听死信队列，当到达指定延迟时间后，正常队列中的消息就会被转发到死信队列从而被消费，实现延迟消息的效果。此方式实现的延迟消息存在一定的缺陷：正常队列中的所有消息延迟时间都是相同的，如果想要实现不同的延迟时间，就需要不断地增加正常队列。

另一种方式是前文介绍的直接由生产者发送带 `TTL` 过期时间的消息，正常队列不设置过期时间，从而实现不同延迟时间，但是这样也会存在问题，不同延迟时间的消息在正常队列中是有序的，如果先发送一个延迟 `10` 秒的消息后发送一个延迟 `2` 秒的消息，`RabbitMQ` 只会检查处于队首位置的消息是否到达过期时间，后面的消息不会被检查，所以就导致后发送的延迟 `2` 秒的消息会一直等待先发送的延迟 `10` 秒的消息被转发走才被转发，从而出现消息延迟时间不准确的问题。

## 基于插件的延迟消息

使用 `RabbitMQ` 社区提供的插件 [`rabbitmq-delayed-message-exchange`](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange) 来实现延迟消息。下面基于 `Docker` 来安装使用该插件。

### 制作镜像

从 [https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/latest](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/latest) 上下载最新 `.ez` 插件文件并存放至 `/plugins` 目录，然后使用命令 `rabbitmq-plugins enable --offline rabbitmq_delayed_message_exchange` 开启延迟消息插件。`Dockerfile` 文件如下：

```dockerfile
# 2022-05-04 21:11:39 最新rabbitmq-delayed-message-exchange插件支持的rabbitmq server版本为3.9.x.
# FROM rabbitmq:management-alpine
FROM rabbitmq:3.9.16-management-alpine

ARG URL=https://api.github.com/repos/rabbitmq/rabbitmq-delayed-message-exchange/releases/latest
RUN apk add curl jq wget && \
  VERSION=`curl $URL | jq -r ".tag_name"` && \
  wget -P /plugins https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/${VERSION}/rabbitmq_delayed_message_exchange-${VERSION}.ez && \
  rabbitmq-plugins enable --offline rabbitmq_delayed_message_exchange

LABEL maintainer=admin@lilu.org.cn
```

> 制作镜像命令参考：`docker build -t llnancy/rabbitmq-management-delayed:latest .`
>
> 参见：
>
> - [https://github.com/llnancy/awesome-dockerfile/blob/master/rabbitmq/Dockerfile](https://github.com/llnancy/awesome-dockerfile/blob/master/rabbitmq/Dockerfile)
> - [https://github.com/llnancy/awesome-dockerfile/blob/master/.github/workflows/rabbitmq-build.yml](https://github.com/llnancy/awesome-dockerfile/blob/master/.github/workflows/rabbitmq-build.yml)

### Docker Compose 启动

使用 `Docker Componse` 工具编排，`docker-compose.yml` 文件内容如下：

```yml
version: "3.9"

services:
  rabbitmq-management:
    restart: always
    image: llnancy/rabbitmq-management-delayed:latest
    container_name: rabbitmq
    ports:
      - 15672:15672
      - 5672:5672
    environment:
      - TZ=Asia/Shanghai
```

执行命令：

```yml
docker-compose -f docker-compose.yml up -d
```

访问 `http://127.0.0.1:15672` 即可看到 `RabbitMQ` 控制台。

### 插件基本原理

基于插件的延迟消息原理图如下：

![延迟消息原理图](https://posts-cdn.lilu.org.cn/2022/05/0514a98b7b0944a0e96286a0d74091ebab768920.png)

核心是一个类型为 `x-delayed-message` 的交换机，该交换机会先将消息保存至 `Mnesia`（`Erlang` 语言生态中的一个分布式数据库管理系统），然后尝试确认消息是否过期，如果过期，则将消息投递至绑定的队列，整个延迟消息过程结束。

### 基本信息配置

在属性配置信息类 `RabbitMQProperties` 中添加延迟交换机、延迟队列及绑定键字段。代码如下：

```java
package io.github.llnancy.xuejian.rabbitmq.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 属性配置信息类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/25
 */
@Configuration
@ConfigurationProperties(prefix = "my.rabbitmq")
@Data
public class RabbitMQProperties {
    
    // ...省略前文字段
    
    /**
     * 延迟交换机名称
     */
    private String delayedExchangeName;

    /**
     * 延迟队列名称
     */
    private String delayedQueueName;

    /**
     * 延迟队列绑定键
     */
    private String delayedRoutingKey;
}
```

然后在 `application.yml` 文件中进行配置。示例如下：

```yml
my:
  rabbitmq:
    delayed-exchange-name: delayed_exchange
    delayed-queue-name: delayed_queue
    delayed-routing-key: delayed.#
```

### 延迟交换机关系配置

配置延迟交换机、队列及它们之间的绑定关系。

创建延迟交换机关系配置类 `DelayedMessageConfig`，编写代码如下：

```java
package io.github.llnancy.xuejian.rabbitmq.config;

import com.google.common.collect.Maps;
import io.github.llnancy.xuejian.rabbitmq.config.property.RabbitMQProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 基于 rabbitmq-delayed-message-exchange 插件的延迟消息配置类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/5
 */
@Configuration
public class DelayedMessageConfig {

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    /**
     * x-delayed-message类型的交换机
     *
     * @return CustomExchange
     */
    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> arguments = Maps.newHashMapWithExpectedSize(1);
        arguments.put("x-delayed-type", ExchangeTypes.TOPIC);
        return new CustomExchange(rabbitMQProperties.getDelayedExchangeName(), "x-delayed-message", true, false, arguments);
    }

    /**
     * 延迟队列
     *
     * @return Queue
     */
    @Bean
    public Queue delayedQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getDelayedQueueName())
                .build();
    }

    /**
     * 延迟交换机和队列进行绑定
     *
     * @return Binding
     */
    @Bean
    public Binding bindingDelayedQueueExchange() {
        return BindingBuilder.bind(delayedQueue())
                .to(delayedExchange())
                .with(rabbitMQProperties.getDelayedRoutingKey())
                .noargs();
    }
}
```

### 延迟消息投递

通过 `MessagePostProcessor` 类为消息设置 `x-delay` 延迟时间。示例用法如下：

```java
public void send(MsgDTO msgDTO, String routingKey, Integer expire) {
    rabbitTemplate.convertAndSend(
            rabbitMQProperties.getDelayedExchangeName(),
            routingKey,
            msgDTO,
            message -> {
                MessageProperties messageProperties = message.getMessageProperties();
                messageProperties.setDelay(expire); // 消息延迟时间，单位ms。
                return message;
            },
            new CorrelationData()
    );
}
```

同样地，我们暴露一个 `HTTP` 接口便于触发延迟消息的发送。代码如下：

```java
@PostMapping("/send-delayed")
public void send(@RequestBody MsgDTO msgDTO, String routingKey, Integer delay) {
    rabbitMQProducer.send(msgDTO, routingKey, delay);
}
```

启动服务后使用命令 `curl --location --request POST 'localhost:8080/send-delayed?routingKey=delayed.xxx&delay=3000' --header 'Content-Type: application/json' --data-raw '{"msg": "xxx"}'` 往延迟交换机中发送一条 `delay=3s` 的消息。

### 消费延迟消息

由于消息体为 `MsgDTO`，且消费逻辑一致。可直接在消费者 `RabbitMQConsumer` 中的 `@RabbitListener` 注解上添加 `${my.rabbitmq.delayed-queue-name}` 延迟队列进行消费。代码示例如下：

```java
@RabbitListener(queues = {
        "${my.rabbitmq.topic-queue-name}",
        // "${my.rabbitmq.normal-queue-name}",
        "${my.rabbitmq.dead-letter-queue-name}",
        "${my.rabbitmq.delayed-queue-name}"
})
```

重启服务后使用命令 `curl --location --request POST 'localhost:8080/send-delayed?routingKey=delayed.xxx&delay=3000' --header 'Content-Type: application/json' --data-raw '{"msg": "xxx"}'` 往延迟交换机中发送一条 `delay=3s` 的消息。控制台输出如下：

```shell
2022-05-05 17:11:06.845 ERROR 41081 --- [nectionFactory1] i.g.l.x.rabbitmq.config.RabbitMQCallback   : RabbitMQ - [ReturnsCallback] 交换机路由消息至队列失败，消息退回发起者，消息内容为：ReturnedMessage [message=(Body:'{"msg":"xxx"}' MessageProperties [headers={spring_returned_message_correlation=ea0839b0-9f6e-4efd-88e9-c449e04f8092, __TypeId__=io.github.llnancy.xuejian.rabbitmq.model.MsgDTO}, contentType=application/json, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, receivedDelay=3000, deliveryTag=0]), replyCode=312, replyText=NO_ROUTE, exchange=delayed_exchange, routingKey=delayed.xxx]
2022-05-05 17:11:06.849  INFO 41081 --- [nectionFactory1] i.g.l.x.rabbitmq.config.RabbitMQCallback   : RabbitMQ - [ConfirmCallback] 生产端ID=ea0839b0-9f6e-4efd-88e9-c449e04f8092的消息投递至交换机 成功
2022-05-05 17:11:09.900  INFO 41081 --- [ntContainer#0-1] i.g.l.x.r.mq.consumer.RabbitMQConsumer     : RabbitMQ - [RabbitMQConsumer] 消费端 消费到消息，msg=MsgDTO(msg=xxx), message=(Body:'{"msg":"xxx"}' MessageProperties [headers={spring_listener_return_correlation=021fc745-7417-4cba-9f6c-b7bdd32209f3, spring_returned_message_correlation=ea0839b0-9f6e-4efd-88e9-c449e04f8092, __TypeId__=io.github.llnancy.xuejian.rabbitmq.model.MsgDTO}, contentType=application/json, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=delayed_exchange, receivedRoutingKey=delayed.xxx, receivedDelay=3000, deliveryTag=1, consumerTag=amq.ctag-4hR_XSMUwvEYTX1lvrNx4Q, consumerQueue=delayed_queue])
```

可看到消息被延迟了 `3` 秒才被消费到。同时我们注意到控制台还输出了 `ReturnsCallback` 的回调日志，原因是延迟交换机确实没有将消息路由至队列，而是将消息暂存至了 `Mnesia` 中，等延迟时间到了才会路由至队列。建议在 `ReturnsCallback` 回调中根据交换机名称进行判断处理，示例如下：

```java
/**
 * 交换机路由消息至队列失败 - 消息回退回调
 * 需要开启 spring.rabbitmq.publisher-returns 参数，设置为 true。
 * 同时需要配合 spring.rabbitmq.template.mandatory 参数使用，也设置为 true。
 *
 * @param returned 退回的消息
 */
@Override
public void returnedMessage(@NonNull ReturnedMessage returned) {
    if (rabbitMQProperties.getDelayedExchangeName().equals(returned.getExchange())) {
        // 处理 rabbitmq-delayed-message-exchange 插件的延迟交换机回调
        return;
    }
    log.error("RabbitMQ - [ReturnsCallback] 交换机路由消息至队列失败，消息退回发起者，消息内容为：{}", returned);
}
```

> 个人建议：如果业务只需要固定延迟时间的延迟消息则优先选择基于死信+延迟队列的实现；否则选用插件。注意插件也存在一定局限性，如果业务中包含百万级别以上的延迟消息，该插件也不能保证延迟时间的绝对准确。详见 [https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/issues/72](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/issues/72)

# 其它类型队列

## 优先队列

消息可按照指定的优先级进行顺序消费。它需要将队列设置为优先队列（调用 `QueueBuilder#maxPriority` 方法设置 `x-max-priority` 参数），同时消息发送时给消息指定优先级（调用 `MessageProperties#setPriority`），然后将所有消息全部发送至队列后才能启动消费者进行消费（全部发送至队列后才能按照优先级进行排序，否则会是发一条消费一条）。实际应用场景并不多。

## 惰性队列

惰性队列会将消息尽可能的存储在磁盘中，只有当消费者消费到对应消息时才会加载到内存，极大的减小了内存开销，但同时也降低了消费者的消费速度。另外一点是消息存储在磁盘可以让 `RabbitMQ` 存储更多的消息。

> 设置惰性队列：调用 `QueueBuilder#lazy` 方法设置 `x-queue-mode` 参数为 `lazy`。

## 镜像队列

`RabbitMQ` 主从集群中的主节点如果挂掉，其队列中未被消费的消息就会丢失，无法自动进行故障转移。镜像队列的做法是将主节点队列中的消息备份至集群其它节点上，形成整个集群的高可用。具体设置方法可参考 [https://www.rabbitmq.com/parameters.html#policies](https://www.rabbitmq.com/parameters.html#policies)。
