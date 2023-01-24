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