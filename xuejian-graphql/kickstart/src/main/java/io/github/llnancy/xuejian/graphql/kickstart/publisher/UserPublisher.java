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
