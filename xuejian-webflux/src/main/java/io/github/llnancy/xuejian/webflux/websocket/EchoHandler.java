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
