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

        // throw new GraphQLException("SQL Error");
        // throw new RuntimeException("SQL Error");

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
