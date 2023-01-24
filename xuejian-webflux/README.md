* [响应式流（Reactive Streams）规范](#响应式流reactive-streams规范)
* [Reactive Streams API](#reactive-streams-api)
* [背压（Backpressure）机制](#背压backpressure机制)
* [响应式编程](#响应式编程)
* [Project Reactor](#project-reactor)
   * [Flux](#flux)
   * [Mono](#mono)
   * [subscribe](#subscribe)
   * [BaseSubscriber](#basesubscriber)
   * [编程方式创建序列](#编程方式创建序列)
      * [同步创建](#同步创建)
      * [异步多线程创建（create）](#异步多线程创建create)
      * [异步单线程创建（push）](#异步单线程创建push)
         * [推拉结合模式](#推拉结合模式)
         * [handle 方法](#handle-方法)
* [Spring WebFlux](#spring-webflux)
* [CRUD 实战](#crud-实战)
* [响应式数据访问](#响应式数据访问)
   * [整合 MongoDB](#整合-mongodb)
   * [整合 Redis](#整合-redis)
   * [整合 WebSocket](#整合-websocket)
   * [整合 R2DBC](#整合-r2dbc)
* [单元测试](#单元测试)
   * [响应式流单元测试](#响应式流单元测试)
   * [Repository 层单元测试](#repository-层单元测试)
   * [Service 层单元测试](#service-层单元测试)
   * [Controller 层单元测试](#controller-层单元测试)
* [总结](#总结)

# 响应式流（Reactive Streams）规范

`Reactive Streams` 的目的是为具有非阻塞背压的异步流处理提供一个标准。主要目标是管理跨异步边界的流数据交换 – 考虑将元素传递给另一个线程或线程池 — 同时确保接收方不会被迫缓冲任意数量的数据。换句话说，背压是这个模型的一个组成部分，以便让线程之间的中介队列受到约束。

总之，`Reactive Streams` 是 `JVM` 的面向流的库的标准和规范，它可以：

- 处理可能无限数量的元素
- 按顺序处理
- 在组件之间异步传递元素
- 具有强制性非阻塞背压

`Reactive Streams` 规范由以下部分组成：

- **`API`**：统一的 `Reactive Streams` 接口。
- **`Technology Compatibility Kit (TCK)`**：用于实现一致性测试的标准测试套件。

# Reactive Streams API

`Reactive Streams API` 定义了以下组件：

- `Publisher`

```java
public interface Publisher<T> {
    public void subscribe(Subscriber<? super T> s);
}
```

`Publisher` 是可以发布无限数量的序列元素的提供者，根据从其订阅者（`Subscriber`）收到的需求进行发布。

- `Subscriber`

```java
public interface Subscriber<T> {
    public void onSubscribe(Subscription s);
    public void onNext(T t);
    public void onError(Throwable t);
    public void onComplete();
}
```

`Subscriber` 是可以从发布者那里订阅并接收元素的订阅者。其中，`onSubscribe` 方法是发布者调用 `subscribe` 方法时的回调，包含 `Subscription` 订阅上下文对象，上下文中包含本次回调订阅者想向发布者请求的元素个数。当订阅关系建立后，发布者可以调用订阅者的 `onNext` 方法向订阅者发送一个元素，这个过程是持续不断的，直到达到 `Subscription` 上下文对象中所请求的元素个数，此时触发 `onComplete` 方法，表示整个数据流发送完成。整个过程一旦出现异常，就会触发 `onError` 方法。

- `Subscription`

```java
public interface Subscription {
    public void request(long n);
    public void cancel();
}
```

`Subscription` 是订阅上下文对象，它在发布者和订阅者之间传输，确保了发布者和订阅者针对数据处理速度达成动态平衡，即背压机制。`request` 方法用于请求 `n` 个元素；`cancel()` 方法用于取消本次订阅。

- `Processor`

```java
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {
}
```

`Processor` 是一个处理阶段，它既是 `Subscriber` 又是 `Publisher`，必须同时遵循两者的规范。

为了实现全异步，这些接口定义的所有方法都没有返回值。

# 背压（Backpressure）机制

订阅者可以根据自身当前的处理能力通知发布者调整生产流元素的速度。

# 响应式编程

响应式编程（`Reactive Programming`）是基于异步和事件驱动的非阻塞程序。

`Reactive Streams` 规范的主流实现框架有 `RxJava`、`Akka`、`Vert.x` 和 `Project Reactor` 等。其中 `RxJava` 诞生于响应式流规范之前，`2.x` 版本按照响应式流规范进行了重构。而 `Project Reactor` 诞生于响应式流规范之后，严格按照响应式流规范进行 `API` 设计与实现，所以 `Spring WebFlux` 选择了集成 `Project Reactor` 框架。

# Project Reactor

响应式流规范的基本组件是一个异步的数据序列，在 `Project Reactor` 框架中，我们可以把这个异步数据序列表示为以下形式：

![asyc-data-sequence](https://posts-cdn.lilu.org.cn/2023/01/asyc-data-sequence.png)

发布者可以通过向其订阅者推送数据（调用 `onNext`），也可以发出错误（调用 `onError`）或完成信号（调用 `onComplete`），错误和完成信号都会终止序列。语义表示如下：

```text
onNext x 0..N [onError | onComplete]
```

`Project Reactor` 框架提供了两个核心组件来发布数据序列：`Flux` 和 `Mono`。`Flux` 对象表示 `0` 到 `N` 个元素的异步序列；而 `Mono` 对象表示 `0` 个或 `1` 个元素的异步序列。

## Flux

下图展示了 `Flux` 如何转化元素：

![flux](https://posts-cdn.lilu.org.cn/2023/01/flux.svg)

`Flux` 是一个标准的 `Publisher<T>` 实现，表示 `0` 到 `N` 个元素的异步序列，可选择完成信号或错误终止。

使用示例：

```java
// 通过 Flux.just 静态方法枚举元素创建字符串序列
Flux<String> seq1 = Flux.just("foo", "bar", "foobar");

// 通过 Flux.fromIterable 静态方法从集合中创建字符串序列
List<String> iterable = Arrays.asList("foo", "bar", "foobar");
Flux<String> seq2 = Flux.fromIterable(iterable);

// 从数字 5 开始，生成 3 个元素的序列。
Flux<Integer> numbersFromFiveToSeven = Flux.range(5, 3);
```

## Mono

下图展示了 `Mono` 如何转化元素：

![mono](https://posts-cdn.lilu.org.cn/2023/01/mono.svg)

`Mono<T>` 是一个特殊的 `Publisher<T>`，它通过 `onNext` 信号最多发出一个元素，然后以 `onComplete` 信号终止（成功的 `Mono`，有或没有值），或者只发出一个 `Error` 信号（失败的 `Mono`）。

大多数 `Mono` 实现都应在调用 `onNext` 后立即在其订阅者上调用 `onComplete`。`Mono.never()` 是一个异常值：它不发出任何信号，一般用来测试。另外，明确禁止组合使用 `onNext` 和 `onError`。

`Mono` 仅提供可用于 `Flux` 的运算符的子集，并且一些运算符（特别是那些将 `Mono` 与另一个 `Publisher` 组合在一起的运算符）切换到 `Flux`。例如，`Mono#concatWith(Publisher)` 返回一个 `Flux`；而 `Mono.then(Mono)` 返回另一个 `Mono`。

> 可使用 `Mono<Void>` 表示只有完成概念的无返回值异步进程（类似于 `Runnable`）。

使用示例：

```java
// 0 个元素的 Mono
Mono<String> noData = Mono.empty(); 

// 1 个元素的 Mono
Mono<String> data = Mono.just("foo");
```

## subscribe

`Flux` 和 `Mono` 基于 `Java8 Lambda` 提供了多个重载的 `subscribe()` 订阅方法，签名如下：

```java
/**
 * 订阅并触发序列。
 * 该方法未指定链中事件的任何消费行为，尤其是没有错误处理，因此通常应首选其它重载方法。
 */
Disposable subscribe(); 

/**
 * 可对序列中每个元素产生的值做一些处理
 */
Disposable subscribe(Consumer<? super T> consumer); 

/**
 * 处理元素值和异常
 */
Disposable subscribe(Consumer<? super T> consumer,
                     Consumer<? super Throwable> errorConsumer); 

/**
 * 处理元素值和异常，同时在序列成功完成时做其它处理。
 */
Disposable subscribe(Consumer<? super T> consumer,
                     Consumer<? super Throwable> errorConsumer,
                     Runnable completeConsumer); 

/**
 * 不仅处理元素值、异常及成功完成时的处理，而且还可以对该订阅调用产生的订阅做处理。
 */
Disposable subscribe(Consumer<? super T> consumer,
                     Consumer<? super Throwable> errorConsumer,
                     Runnable completeConsumer,
                     Consumer<? super Subscription> subscriptionConsumer);
```

方法返回对订阅的引用，可以在不需要更多数据时使用该引用取消订阅。取消后，源应停止产生值并清理它创建的所有资源。这种取消和清理行为在 `Reactor` 中由通用的 `Disposable` 接口表示，可以调用其 `dispose()` 方法取消订阅。

对于 `Flux` 或 `Mono`，取消是源应停止生成元素的信号。但是，它不能保证是立即的：某些源可能会非常快地生成元素，甚至在收到取消指令之前就可以完成。

使用示例：

```java
// 生成 3 个元素的序列
Flux<Integer> ints = Flux.range(1, 3);

// 订阅
ints.subscribe();

// 订阅并对每一个元素处理
ints.subscribe(System.out::println);

// 生成 4 个元素的序列，并在获取第 4 个元素时触发错误。
ints = Flux.range(1, 4)
        .map(i -> {
            if (i <= 3) return i;
            throw new RuntimeException("Got to 4");
        });

// 订阅，对每一个元素处理，同时处理错误信号
ints.subscribe(System.out::println, err -> System.out.println("Error: " + err));

// 错误信号和完成信号都是终端事件，并且互斥（不会同时得到）。为了演示 completeConsumer，这里重新生成序列。
ints = Flux.range(1, 4);

// 订阅，对每一个元素处理，同时处理可能出现的错误信号，并且在序列成功完成时做处理。
ints.subscribe(
        System.out::println,
        err -> System.out.println("Error: " + err),
        () -> System.out.println("Done")
);
```

## BaseSubscriber

`BaseSubscriber` 是 `Flux` 和 `Mono` 类中 `subscribe` 方法的替代品。它更加通用，其实例（或子类）具有一次性：如果 `BaseSubscriber` 订阅了第二个 `Publisher`，那么它会取消对第一个 `Publisher` 的订阅。

使用示例：

```java
public class BaseSubscriberExample {

    public static void main(String[] args) {
        SampleSubscriber<Integer> ss = new SampleSubscriber<>();
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(ss);
    }

    public static class SampleSubscriber<T> extends reactor.core.publisher.BaseSubscriber<T> {

        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            System.out.println("Subscribed");
            request(1);
        }

        @Override
        protected void hookOnNext(T value) {
            System.out.println(value);
            request(1);
        }
    }
}
```

## 编程方式创建序列

通过编程方式创建 `Flux` 和 `Mono` 序列，包括同步创建和异步创建，其中异步创建又有多线程和单线程之分。所有的方法都提供了一个称为 `sink`（发射器） 的 `API` 来触发事件。

### 同步创建

同步创建使用 `generate` 静态方法。发射器 `sink` 是 `SynchronousSink`，其 `next` 方法每次回调最多只能调用一次。

使用示例：

```java
Flux<String> flux = Flux.generate(
        () -> 0,
        (state, sink) -> {
            sink.next("3 x " + state + " = " + 3 * state);
            if (state == 10) sink.complete();
            return state + 1;
        }
);
```

- 第一个参数用于提供初始状态值 `0`。
- 第二个参数用于生成序列中的元素，根据提供的初始状态值进行生成，并选择性停止，每次返回在下一轮调用中使用的新状态。

也可以使用可变状态对象作为初始状态：

```java
Flux<String> flux = Flux.generate(
        AtomicLong::new,
        (state, sink) -> {
            long i = state.getAndIncrement();
            sink.next("3 x " + i + " = " + 3 * i);
            if (i == 10) sink.complete();
            // 每次返回同一个实例作为新状态
            return state;
        }
);
```

如果可变状态对象需要清理一些资源（例如数据库连接等），可以使用重载的 `generate(Supplier<S>, BiFunction, Consumer<S>)` 方法清理最后一个状态实例：

```java
Flux<String> flux = Flux.generate(
        AtomicLong::new,
        (state, sink) -> {
            long i = state.getAndIncrement();
            sink.next("3 x " + i + " = " + 3 * i);
            if (i == 10) sink.complete();
            return state;
        },
        (state) -> System.out.println("state: " + state)
);
```

### 异步多线程创建（create）

异步多线程创建使用 `create` 静态方法。发射器 `sink` 是 `FluxSink`。通常用于将现有 `API` 桥接到响应式编程，例如基于监听器的异步 `API`。

假设现在有一个基于监听器的 `API`，它按块处理数据并有两个事件：

1. 数据块准备就绪事件
2. 处理完成事件

`API` 定义如下：

```java
public interface MyEventListener<T> {

    void onDataChunk(List<T> chunk);

    void processComplete();
}
```

我们可以使用 `create` 方法将其桥接至 `Flux<T>` 中：

```java
Flux<String> bridge = Flux.create(sink -> {
    new MyEventListener<String>() {

        @Override
        public void onDataChunk(List<String> chunk) {
            for (String s : chunk) {
                // 数据块中的每一个元素都被桥接为 Flux 中的元素
                sink.next(s);
            }
        }

        @Override
        public void processComplete() {
            // 转化为 onComplete 事件
            sink.complete();
        }
    };
    // process event listener
});
```

此外，`create` 还可以管理背压，通过调整 `OverflowStrategy` 溢出策略来优化背压行为：

- `IGNORE`：忽略下游背压请求。当下游队列满时，可能会产生 `IllegalStateException` 异常。
- `ERROR`：当下游无法跟上消费时发出 `IllegalStateException` 异常信号。
- `DROP`：如果下游还没有准备好接受序列则丢弃传入信号。
- `LATEST`：下游只接收来自上游的最新信号。
- `BUFFER`：默认策略。在下游无法跟上消费时缓冲所有信号。（无限缓冲并可能导致 `OutOfMemoryError` 异常）

> `Mono` 也提供了 `create` 方法，`Mono` 创建的 `MonoSink` 不允许多次发射，它会在第一个信号之后丢弃所有信号。

### 异步单线程创建（push）

`push` 是 `generate` 和 `create` 之间的中间地带，适用于处理来自单个生产者的事件。它在某种意义上类似于 `create`，也可以是异步的，并且可以使用 `create` 支持的任何溢出策略来管理背压。但是，一次只有一个生产线程可以调用 `next`、`complete` 或 `error`。

使用示例：

```java
Flux<String> bridge = Flux.push(sink -> {
    myEventProcessor.register(
            new SingleThreadEventListener<String>() {

                @Override
                public void onDataChunk(List<String> chunk) {
                    for (String s : chunk) {
                        sink.next(s);
                    }
                }

                @Override
                public void processComplete() {
                    sink.complete();
                }

                @Override
                public void processError(Throwable t) {
                    sink.error(t);
                }
            }
    );
});
```

#### 推拉结合模式

大多数 `Reactor` 操作符，如 `create` 和 `push`，都遵循混合推/拉模型：默认是推模型，但允许主动拉取。通过设置一个 `onRequest` 消费者，管理主动拉取的数量并确保只有在有待处理的请求时才通过 `sink` 发射器推送数据。

使用示例：

```java
Flux<String> bridge = Flux.create(sink -> {
    myMessageProcessor.register(
            new MyMessageListener<String>() {

                @Override
                public void onMessage(List<String> messages) {
                    for (String message : messages) {
                        sink.next(message);
                    }
                }
            }
    );
    sink.onRequest(n -> {
        List<String> messages = myMessageProcessor.getHistory(n);
        for (String message : messages) {
            sink.next(message);
        }
    });
});
```

#### handle 方法

`handle` 是一个实例方法。和 `generate` 方法类似，它使用 `SynchronousSink` 同步生成元素。但是，`handle` 可用于从每个源元素生成任意值，也可以跳过一些元素。可以看做是 `map` 和 `filter` 的结合。方法签名如下：

```java
<R> Flux<R> handle(BiConsumer<? super T, SynchronousSink<R>> handler);
```

使用示例：

```java
public class HandleExample {

    public static void main(String[] args) {
        Flux<String> alphabet = Flux.just(-1, 30, 13, 9, 20)
                .handle((i, sink) -> {
                    String letter = alphabet(i);
                    if (letter != null) {
                        sink.next(letter);
                    }
                });
        alphabet.subscribe(System.out::println);
    }

    public static String alphabet(int letterNumber) {
        if (letterNumber < 1 || letterNumber > 26) {
            return null;
        }
        int letterIndexAscii = 'A' + letterNumber - 1;
        return "" + (char) letterIndexAscii;
    }
}
```

# Spring WebFlux

`Spring 5` 提供的 `WebFlux` 模块基于 `Project Reactor` 实现。包含对响应式 `HTTP` 和 `WebSocket` 客户端的支持。类似于 `Servlet API` , `WebFlux` 提供了 `WebHandler API` 去定义非阻塞 `API` 抽象接口。

核心依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

# CRUD 实战

创建 `Spring Boot` 项目 `xuejian-webflux`，引入 `webflux` 相关依赖。完整 `pom.xml` 如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>io.github.llnancy</groupId>
        <artifactId>xuejian-parent</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>xuejian-webflux</artifactId>
    <description>Spring Boot Webflux 实战</description>

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
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
    </dependencies>
</project>
```

以用户 `User` 实体为例进行 `CRUD` 代码编写：

实体类 `User.java`：

```java
package io.github.llnancy.xuejian.webflux.crud.entity;

import lombok.Data;

/**
 * user entity
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Data
public class User {

    private Long id;

    private String username;

    private String password;
}
```

数据访问层 `UserRepository.java`（暂时在内存中模拟数据库）：

```java
package io.github.llnancy.xuejian.webflux.crud.repository;

import io.github.llnancy.xuejian.webflux.crud.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * user repository
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Repository
public class UserRepository {

    private final ConcurrentMap<Long, User> repository = new ConcurrentHashMap<>();

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    public Long save(User user) {
        long id = ID_GENERATOR.incrementAndGet();
        user.setId(id);
        repository.put(id, user);
        return id;
    }

    public Collection<User> findAll() {
        return repository.values();
    }

    public User findById(Long id) {
        return repository.get(id);
    }

    public void deleteById(Long id) {
        repository.remove(id);
    }
}
```

业务层 `UserService.java`：

```java
package io.github.llnancy.xuejian.webflux.crud.service;

import io.github.llnancy.xuejian.webflux.crud.entity.User;
import io.github.llnancy.xuejian.webflux.crud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * user service
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<Long> save(User user) {
        return Mono.create(sink -> sink.success(userRepository.save(user)));
    }

    public Mono<User> findById(Long id) {
        return Mono.justOrEmpty(userRepository.findById(id));
    }

    public Flux<User> findAll() {
        return Flux.fromIterable(userRepository.findAll());
    }

    public Mono<Void> deleteById(Long id) {
        return Mono.create(sink -> {
            userRepository.deleteById(id);
            sink.success();
        });
    }
}
```

控制器 `UserController.java`：

```java
package io.github.llnancy.xuejian.webflux.crud.controller;

import io.github.llnancy.xuejian.webflux.crud.entity.User;
import io.github.llnancy.xuejian.webflux.crud.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * user controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    public Mono<Long> save(@RequestBody User user) {
        return userService.save(user);
    }

    @GetMapping("/user/{id}")
    public Mono<User> findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/users")
    public Flux<User> findAll() {
        return userService.findAll();
    }

    @DeleteMapping("/user/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return userService.deleteById(id);
    }
}
```

启动类 `XueJianWebfluxApplication.java`：

```java
package io.github.llnancy.xuejian.webflux;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * webflux application 启动器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@SpringBootApplication
public class XueJianWebfluxApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(XueJianWebfluxApplication.class)
                .web(WebApplicationType.REACTIVE)
                .run(args);
    }
}
```

`webflux` 需设置 `web` 环境类型为 `WebApplicationType.REACTIVE`，默认通过 `netty` 启动，端口号为 `8080`。

启动完成后，可通过以下 `curl` 命令（或 `postman` 等工具）进行测试：

```shell
// save
curl --location --request POST 'http://localhost:8080/user' --header 'Content-Type: application/json' --data-raw '{"username": "webflux", "password": "123456"}'
// findById
curl --location --request GET 'http://localhost:8080/user/1'
// findAll
curl --location --request GET 'http://localhost:8080/users'
// deleteById
curl --location --request DELETE 'http://localhost:8080/user/1'
```

# 响应式数据访问

想要实现真正的响应式编程，整个请求链路必须都是响应式，不能产生任何同步阻塞行为。在 `Web` 服务中，传统的关系型数据库都是基于非响应式（同步阻塞）的数据访问机制，返回实体对象，而不是响应式的 `Flux/Mono` 流。

从 `Spring Boot 2.x` 版本开始，针对支持响应式访问的各种数据库，`Spring Data` 提供了响应式版本的 `Repository` 支持。包括 `MongoDB`、`Cassandra`、`Redis`、`Couchbase` 等。另外，还针对响应式关系型数据库连接 `R2DBC` 规范封装了 `Spring Data R2DBC` 模块。

## 整合 MongoDB

核心依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
```

> 可使用 `Docker Compose` 工具一键安装 `MongoDB`。
> `docker-compose.yml` 文件参考 [https://github.com/llnancy/xuejian/blob/master/xuejian-webflux/src/main/resources/docker-compose/mongo/docker-compose.yml](https://github.com/llnancy/xuejian/blob/master/xuejian-webflux/src/main/resources/docker-compose/mongo/docker-compose.yml)。
> 启动命令：`docker-compose -f docker-compose.yml up -d`。

`application.yml` 配置文件：

```yml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: admin
      username: root
      # 注意：此处如果是纯数字需要加上双引号
      password: "123456"
```

同样，我们以用户实体为例进行 `CRUD` 代码编写：

实体类 `MongoUser.java`：

```java
package io.github.llnancy.xuejian.webflux.mongo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * mongo user
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Data
@Document("user")
public class MongoUser {

    @Id
    private String id;

    private String username;

    private String password;
}
```

数据访问层 `MongoUserRepository.java`：

```java
package io.github.llnancy.xuejian.webflux.mongo.repository;

import io.github.llnancy.xuejian.webflux.mongo.entity.MongoUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link MongoUser} repository
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Repository
public interface MongoUserRepository extends ReactiveMongoRepository<MongoUser, String> {
}
```

> 除 `ReactiveMongoRepository` 外还可以使用 `ReactiveMongoTemplate` 自行实现 `Repository`。

业务层 `MongoUserService.java`：

```java
package io.github.llnancy.xuejian.webflux.mongo.service;

import io.github.llnancy.xuejian.webflux.mongo.entity.MongoUser;
import io.github.llnancy.xuejian.webflux.mongo.repository.MongoUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link MongoUser} service
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/24
 */
@Service
@AllArgsConstructor
public class MongoUserService {

    private final MongoUserRepository repository;

    public Mono<MongoUser> save(@RequestBody MongoUser user) {
        return repository.save(user);
    }

    public Mono<MongoUser> findById(@PathVariable String id) {
        return repository.findById(id);
    }

    public Flux<MongoUser> findAll() {
        return repository.findAll();
    }

    public Mono<Void> deleteById(@PathVariable String id) {
        return repository.deleteById(id);
    }
}
```

控制器 `MongoUserController.java`：

```java
package io.github.llnancy.xuejian.webflux.mongo.controller;

import io.github.llnancy.xuejian.webflux.mongo.entity.MongoUser;
import io.github.llnancy.xuejian.webflux.mongo.service.MongoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link MongoUser} controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/mongo")
public class MongoUserController {

    private final MongoUserService service;

    @PostMapping("/user")
    public Mono<MongoUser> save(@RequestBody MongoUser user) {
        return service.save(user);
    }

    @GetMapping("/user/{id}")
    public Mono<MongoUser> findById(@PathVariable String id) {
        return service.findById(id);
    }

    @GetMapping("/users")
    public Flux<MongoUser> findAll() {
        return service.findAll();
    }

    @DeleteMapping("/user/{id}")
    public Mono<Void> deleteById(@PathVariable String id) {
        return service.deleteById(id);
    }
}
```

同样，启动完成后，可通过以下 `curl` 命令（或 `postman` 等工具）进行测试：

```shell
// save
curl --location --request POST 'http://localhost:8080/mongo/user' --header 'Content-Type: application/json' --data-raw '{"username": "webflux", "password": "123456"}'
// findById（需要获取 save 方法返回的 id，此处不是数字自增。）
curl --location --request GET 'http://localhost:8080/mongo/user/63bfcadc0177827c0bbe14aa'
// findAll
curl --location --request GET 'http://localhost:8080/mongo/users'
// deleteById
curl --location --request DELETE 'http://localhost:8080/mongo/user/63bfcadc0177827c0bbe14aa'
```

## 整合 Redis

和 `MongoDB` 不同的是，`Redis` 不提供响应存储库：没有类似 `ReactiveMongoRepository` 这样的接口供继承。仅能使用 `ReactiveRedisTemplate` 模版类。

核心依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```

> 可使用 `Docker Compose` 工具一键安装 `Redis`。
> `docker-compose.yml` 文件参考 [https://github.com/llnancy/xuejian/blob/master/xuejian-webflux/src/main/resources/docker-compose/redis/docker-compose.yml](https://github.com/llnancy/xuejian/blob/master/xuejian-webflux/src/main/resources/docker-compose/redis/docker-compose.yml)。
> 启动命令：`docker-compose -f docker-compose.yml up -d`。

`application.yml` 配置文件：

```yml
spring:
  redis:
    host: localhost
    port: 6379
    # 密码默认为空
    password:
    timeout: 5000
```

在 `Java` 中，常见的 `Redis` 客户端有两个，一个是基于传统 `Socket API` 开发的 `Jedis`，另一个是基于 `Netty` 框架开发的 `Lettuce`。`Lettuce` 框架是目前唯一支持响应式流的客户端，底层也是基于 `Project Reactor` 实现的。`Spring Boot` 默认客户端是 `Lettuce`。

同样，我们以用户实体为例进行 `CRUD` 代码编写。

实体类 `RedisUser.java`：

```java
package io.github.llnancy.xuejian.webflux.redis.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * redis user
 * must implement the {@link Serializable} interface in the default configuration.
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Data
public class RedisUser implements Serializable {

    private static final long serialVersionUID = 7745741553417591187L;

    private String id;

    private String username;

    private String password;
}
```

注意，由于默认采用的是 `jdk` 序列化，所以实体类 `RedisUser` 必须实现 `java.io.Serializable` 接口，否则会引发 `java.lang.IllegalArgumentException: DefaultSerializer requires a Serializable payload but received an object of type [io.github.llnancy.xuejian.webflux.redis.entity.RedisUser]` 异常。当然也可以自定义序列化方式，例如 `json` 等，示例配置如下：

```java
package io.github.llnancy.xuejian.webflux.redis.config;

import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Configuration
public class RedisConfiguration {

    /**
     * reactiveRedisTemplate serialize config
     *
     * @param connectionFactory {@link ReactiveRedisConnectionFactory}
     * @return {@link ReactiveRedisTemplate}
     * @see RedisReactiveAutoConfiguration#reactiveRedisTemplate(ReactiveRedisConnectionFactory, ResourceLoader)
     */
    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext.<String, Object>newSerializationContext()
                .key(StringRedisSerializer.UTF_8)
                .value(RedisSerializer.json())
                .hashKey(StringRedisSerializer.UTF_8)
                .hashValue(RedisSerializer.json())
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
}
```

控制器 `ReactiveRedisController`：

```java
package io.github.llnancy.xuejian.webflux.redis.controller;

import io.github.llnancy.xuejian.webflux.redis.entity.RedisUser;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * reactive redis controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@RestController
@RequestMapping("/redis")
public class ReactiveRedisController {

    private static final String PREFIX = "user_";

    /**
     * 如果此处指定了泛型，则需要使用 @Resource 注解按 bean 名称注入。
     */
    @Resource
    private ReactiveRedisTemplate<String, RedisUser> reactiveRedisTemplate;

    @PostMapping("/set")
    public Mono<RedisUser> set(@RequestBody RedisUser user) {
        String key = PREFIX + user.getId();
        return reactiveRedisTemplate.opsForValue().getAndSet(key, user);
    }

    @GetMapping("/get/{id}")
    public Mono<RedisUser> get(@PathVariable Long id) {
        return reactiveRedisTemplate.opsForValue().get(PREFIX + id);
    }

    @DeleteMapping("/del/{id}")
    public Mono<Long> del(@PathVariable Long id) {
        return reactiveRedisTemplate.delete(PREFIX + id);
    }
}
```

同样，启动完成后，可通过以下 `curl` 命令（或 `postman` 等工具）进行测试：

```shell
// set
curl --location --request POST 'http://localhost:8080/redis/set' --header 'Content-Type: application/json' --data-raw '{"id": "1", "username": "webflux", "password": "123456"}'
// get
curl --location --request GET 'http://localhost:8080/redis/get/1'
// del
curl --location --request DELETE 'http://localhost:8080/redis/del/1'
```

## 整合 WebSocket

`WebSocket` 是一种通信协议，它支持客户端和服务端双向通讯。类似 `http` 和 `https`，`WebSocket` 的协议标识符为 `ws` 和 `wss`。例如：`ws://localhost:8080/echo`。

我们以一个回声服务为例，编写 `WebSocket` 通信代码：

消息处理器 `EchoHandler.java`：

```java
package io.github.llnancy.xuejian.webflux.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

/**
 * echo websocket handler
 * 实现 {@link WebSocketHandler} 接口处理 websocket 消息
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Component
public class EchoHandler implements WebSocketHandler {

    /**
     * receive() 方法：接收 websocket 消息，返回 Flux 对象。
     * send() 方法：发送消息。
     *
     * @param session {@link WebSocketSession} 对象，可用于获取客户端信息、发送消息和接收消息等操作。
     * @return {@link Mono}
     */
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(session.receive()
                .map(msg -> session.textMessage("server echo: hi, " + msg.getPayloadAsText()))
        );
    }
}
```

配置类 `WebSocketConfiguration.java`：

```java
package io.github.llnancy.xuejian.webflux.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * websocket config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Configuration
public class WebSocketConfiguration {

    @Bean
    public HandlerMapping handlerMapping(EchoHandler echoHandler) {
        Map<String, WebSocketHandler> urlMap = new HashMap<>();
        urlMap.put("/echo", echoHandler);
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        mapping.setUrlMap(urlMap);
        return mapping;
    }
}
```

使用 `Map` 封装 `WebSocket` 协议路由，使用 `SimpleUrlHandlerMapping` 映射路由配置。路由为 `ws://localhost:8080/echo`。

`WebSocket` 的 `Java` 客户端 `WebSocketJavaClient.java`：

```java
package io.github.llnancy.xuejian.webflux.websocket;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.Duration;

/**
 * websocket client
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
public class WebSocketJavaClient {

    public static void main(String[] args) {
        // ReactorNettyWebSocketClient 是 WebFlux 默认 Reactor Netty 库提供的 WebSocketClient 实现
        WebSocketClient client = new ReactorNettyWebSocketClient();
        // 与 ws://localhost:8080/echo 建立 WebSocket 协议连接。
        client.execute(
                URI.create("ws://localhost:8080/echo"),
                // send 方法发送字符串至服务端
                session -> session.send(Flux.just(session.textMessage("websocket")))
                        .thenMany(
                                // receive 方法接收服务端的响应
                                session.receive()
                                        .take(1)
                                        .map(WebSocketMessage::getPayloadAsText)
                        )
                        .doOnNext(System.out::println)
                        .then()
        ).block(Duration.ofMillis(5000));
    }
}
```

先启动服务端后启动客户端即可看到消息通信结果。

## 整合 R2DBC

`R2DBC`（全称 `Reactive Relational Database Connectivity`）将响应式编程 `API` 引入了关系型数据库，它定义了统一的响应式非阻塞 `API` 规范，不同数据库厂商通过实现 `R2DBC` 规范来提供驱动程序包。目前主要有 `H2`、`MariaDB`、`Microsoft SQL Server`、`PostgreSQL` 及 `MySQL` 数据库提供了 `R2DBC` 驱动程序。

> 传统 `JDBC` 规范及其衍生的数据持久层 `ORM` 框架等都是同步阻塞式交互，并不适合集成到响应式编程中。

核心依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-r2dbc</artifactId>
</dependency>
<!-- r2dbc mysql 库 -->
<dependency>
    <groupId>dev.miku</groupId>
    <artifactId>r2dbc-mysql</artifactId>
</dependency>
```

这里我们以 `MySQL` 数据库为例，实现用户实体的持久化及 `CRUD`。

> 可使用 `Docker Compose` 工具一键安装 `MySQL`。
> `docker-compose.yml` 文件参考 [https://github.com/llnancy/xuejian/blob/master/xuejian-webflux/src/main/resources/docker-compose/mysql/docker-compose.yml](https://github.com/llnancy/xuejian/blob/master/xuejian-webflux/src/main/resources/docker-compose/mysql/docker-compose.yml)。
> 启动命令：`docker-compose -f docker-compose.yml up -d`。

`application.yml` 配置文件：

```yml
spring:
  r2dbc:
    url: r2dbc:mysql://127.0.0.1:3306/r2dbc_db?useUnicode=true&characterEncoding=UTF-8&useSSL=false
    username: root
    password: 123456
```

数据库建表语句：

```sql
create database r2dbc_db;
use r2dbc_db;

CREATE TABLE `user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

实体类 `R2dbcUser.java`：

```java
package io.github.llnancy.xuejian.webflux.r2dbc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * r2dbc user
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Table("user")
@Data
public class R2dbcUser {

    @Id
    private Long id;

    private String username;

    private String password;
}
```

数据访问层 `R2dbcUserRepository.java`：

```java
package io.github.llnancy.xuejian.webflux.r2dbc.repository;

import io.github.llnancy.xuejian.webflux.r2dbc.entity.R2dbcUser;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * r2dbc user repository
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
public interface R2dbcUserRepository extends R2dbcRepository<R2dbcUser, Long> {
}
```

控制器 `R2dbcUserController.java`：

```java
package io.github.llnancy.xuejian.webflux.r2dbc.controller;

import io.github.llnancy.xuejian.webflux.r2dbc.entity.R2dbcUser;
import io.github.llnancy.xuejian.webflux.r2dbc.repository.R2dbcUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * r2dbc user controller
 * JDK version can not be too high, we recommend using JDK8.
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/r2dbc")
public class R2dbcUserController {

    private final R2dbcUserRepository repository;

    @PostMapping("/user")
    public Mono<R2dbcUser> save(@RequestBody R2dbcUser user) {
        return repository.save(user);
    }

    @GetMapping("/user/{id}")
    public Mono<R2dbcUser> findById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping("/users")
    public Flux<R2dbcUser> findAll() {
        return repository.findAll();
    }

    @DeleteMapping("/user/{id}")
    public Mono<Void> deleteById(@PathVariable Long id) {
        return repository.deleteById(id);
    }
}
```

同样，启动完成后，可通过以下 `curl` 命令（或 `postman` 等工具）进行测试：

```shell
// save
curl --location --request POST 'http://localhost:8080/r2dbc/user' --header 'Content-Type: application/json' --data-raw '{"username": "webflux", "password": "123456"}'
// findById
curl --location --request GET 'http://localhost:8080/r2dbc/user/1'
// findAll
curl --location --request GET 'http://localhost:8080/r2dbc/users'
// deleteById
curl --location --request DELETE 'http://localhost:8080/r2dbc/user/1'
```

# 单元测试

在一个 `Web` 应用程序中，涉及测试的维度有很多，包括数据访问、服务构建和服务集成等。同时，基于常见的系统分层和代码组织结构，测试工作也体现为一种层次关系，即我们需要测试从 `Repository` 层到 `Service` 层、再到 `Controller` 层的完整业务链路。

而在响应式 `Web` 应用中，因为其推崇的是全栈式的响应式编程模型，所以每一层都需要对响应式组件进行测试。各层所使用的测试方法如下：

| 层            | 测试方式            |
| :------------ | :------------------ |
| 响应式数据流  | StepVerifier 工具类 |
| Repository 层 | @DataMongoTest 注解 |
| Service 层    | Mock 机制           |
| Controller 层 | @WebFluxTest 注解   |

## 响应式流单元测试

`Project Reactor` 框架提供了专门用于测试的 `reactor-test` 组件。依赖如下：

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <scope>test</scope>
</dependency>
```

`reactor-test` 组件的核心类是 `StepVerifier`，使用示例：

```java
@Test
public void test() {
    Flux<String> flux = Flux.just("hello", "webflux");

    // 初始化：将 Flux/Mono 数据流传入 StepVerifier 的 create 方法
    // 正常数据流断言：expectNext/expectNextMatches/assertNext
    // 完成数据流断言：expectComplete
    // 异常数据流断言：expectError/expectErrorMessage
    // 启动测试：verify 方法

    // expectNext
    StepVerifier.create(flux)
            .expectNext("hello")
            .expectNext("webflux")
            .expectComplete()
            .verify();

    // expectNextMatches
    StepVerifier.create(flux)
            .expectSubscription()
            .expectNextMatches(el -> el.equals("hello"))
            .expectNextMatches(el -> el.startsWith("web"))
            .expectComplete()
            .verify();

    // assertNext
    StepVerifier.create(flux)
            .expectSubscription()
            .assertNext(System.out::println)
            .assertNext(System.out::println)
            .expectComplete()
            .verify();

    // concatWith an exception
    flux = flux.concatWith(Mono.error(new IllegalArgumentException("illegal exception!")));

    // expectErrorMessage
    StepVerifier.create(flux)
            .expectNext("hello")
            .expectNext("webflux")
            .expectErrorMessage("illegal exception!")
            .verify();
}
```

## Repository 层单元测试

`pom` 依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

以 `MongoDB` 数据库为例，示例代码如下：

```java
package io.github.llnancy.xuejian.webflux.test;

import io.github.llnancy.xuejian.webflux.mongo.entity.MongoUser;
import io.github.llnancy.xuejian.webflux.mongo.repository.MongoUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeUnit;

/**
 * {@link MongoUserRepository} test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/24
 */
@DataMongoTest
class MongoUserRepositoryTest {

    @Autowired
    private MongoUserRepository userRepository;

    @Autowired
    private ReactiveMongoOperations mongoOperations;

    @BeforeEach
    public void setup() throws InterruptedException {
        mongoOperations.dropCollection(MongoUser.class).subscribe();
        MongoUser mongoUser = new MongoUser();
        mongoUser.setUsername("username1");
        mongoUser.setPassword("password1");
        mongoOperations.insert(mongoUser).subscribe();
        mongoUser.setUsername("username2");
        mongoUser.setPassword("password2");
        mongoOperations.insert(mongoUser).subscribe();
        mongoOperations.findAll(MongoUser.class)
                .subscribe(user -> System.out.println(user.getId()));
        // Just wait for 1 or 2 seconds after inserting on database, because these are asynchronous tasks.
        TimeUnit.SECONDS.sleep(1L);
    }

    @Test
    public void test() {
        Mono<MongoUser> user = userRepository.findMongoUserByUsername("username1");
        StepVerifier.create(user)
                .expectSubscription()
                .expectNextMatches(el -> {
                    Assertions.assertEquals(el.getUsername(), "username1");
                    Assertions.assertEquals(el.getPassword(), "password1");
                    return true;
                })
                .expectComplete()
                .verify();
    }
}
```

`@DataMongoTest` 注解用于测试 `MongoDB` 数据层，默认会扫描 `@Document` 注解标记的类和 `Spring Data MongoRepository` 仓库，另外还会配置 `MongoTemplate` 对象。

> 注意：由于插入操作是响应式的，所以在数据插入完成后需要等待一会儿再进行查询。

## Service 层单元测试

通常我们希望能在不访问真实数据库的前提下测试 `Service` 层中方法的正确性。为此，我们需要通过 `Mock` 机制隔离 `Repository` 层：借助于 `Mockito` 框架提供的 `given/willReturn` 机制。使用示例：

```java
package io.github.llnancy.xuejian.webflux.test;

import io.github.llnancy.xuejian.webflux.mongo.entity.MongoUser;
import io.github.llnancy.xuejian.webflux.mongo.repository.MongoUserRepository;
import io.github.llnancy.xuejian.webflux.mongo.service.MongoUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * {@link MongoUserService} test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/24
 */
@SpringBootTest
class MongoUserServiceTest {

    @Autowired
    private MongoUserService service;

    @MockBean
    private MongoUserRepository repository;

    @Test
    void save() {
        MongoUser user = new MongoUser();
        user.setUsername("username1");
        user.setPassword("password1");
        BDDMockito.given(repository.save(user)).willReturn(Mono.just(user));
        Mono<MongoUser> save = service.save(user);
        StepVerifier.create(save)
                .expectNextMatches(mongoUser -> {
                    Assertions.assertEquals(mongoUser.getUsername(), "username1");
                    Assertions.assertEquals(mongoUser.getPassword(), "password1");
                    return true;
                })
                .verifyComplete();
    }
}
```

## Controller 层单元测试

`Controller` 层的单元测试使用 `@WebFluxTest` 注解，它能够初始化测试 `Controller` 层所必需的 `WebFlux` 基础设施，并且自动注入用于模拟 `HTTP` 请求的 `WebTestClient` 工具类。

`WebTestClient` 工具类专门用于测试 `WebFlux` 组件，无须启动完整的 `HTTP` 服务器。常见方法如下：

- `HTTP` 请求方法：支持 `get`、`post` 和 `delete` 等常见 `HTTP` 方法构造测试请求，使用 `uri()` 方法指定请求路径。
- `exchange()` 方法：发起 `HTTP` 请求，返回一个 `EntityExchangeResult`。
- `expectStatus()` 方法：验证返回状态，通常使用 `isOk()` 方法验证返回状态码是否为 `200`。
- `expectBody()` 方法：验证返回对象体是否为指定对象，并利用 `returnResult()` 方法获取对象。

使用示例：

```java
package io.github.llnancy.xuejian.webflux.test;

import io.github.llnancy.xuejian.webflux.crud.controller.UserController;
import io.github.llnancy.xuejian.webflux.crud.entity.User;
import io.github.llnancy.xuejian.webflux.crud.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

/**
 * {@link UserController} test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@WebFluxTest(value = UserController.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    private static User user;

    @BeforeAll
    public static void beforeAll() {
        user = new User();
        user.setId(1L);
        user.setUsername("websocket");
        user.setPassword("123456");
    }

    @Test
    public void test() {
        BDDMockito.given(userService.save(user))
                .willReturn(Mono.just(1L));
        Long expect = webTestClient.post()
                .uri("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();
        Assertions.assertNotNull(expect);
        Assertions.assertEquals(expect, 1L);
    }
}
```

# 总结

响应式编程代表着未来的一种技术趋势。随着微服务架构的不断发展以及各种中间件技术的日益成熟，响应式编程所提供的异步非阻塞式编程模型非常适合用来构建技术驱动的服务化架构体系。
