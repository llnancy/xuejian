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
