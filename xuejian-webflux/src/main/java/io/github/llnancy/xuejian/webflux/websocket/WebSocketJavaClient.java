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
        WebSocketClient client = new ReactorNettyWebSocketClient();
        client.execute(
                URI.create("ws://localhost:8080/echo"),
                session -> session.send(Flux.just(session.textMessage("websocket")))
                        .thenMany(
                                session.receive()
                                        .take(1)
                                        .map(WebSocketMessage::getPayloadAsText)
                        )
                        .doOnNext(System.out::println)
                        .then()
        ).block(Duration.ofMillis(5000));
    }
}
