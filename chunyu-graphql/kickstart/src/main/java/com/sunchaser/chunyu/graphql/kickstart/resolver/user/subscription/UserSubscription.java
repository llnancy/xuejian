package com.sunchaser.chunyu.graphql.kickstart.resolver.user.subscription;

import com.sunchaser.chunyu.graphql.kickstart.model.User;
import com.sunchaser.chunyu.graphql.kickstart.publisher.UserPublisher;
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
