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
