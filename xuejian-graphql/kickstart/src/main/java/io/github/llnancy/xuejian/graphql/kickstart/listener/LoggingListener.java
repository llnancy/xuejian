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
