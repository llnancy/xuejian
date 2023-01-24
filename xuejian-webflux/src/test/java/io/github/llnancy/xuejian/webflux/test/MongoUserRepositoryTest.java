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
                .verifyComplete();
    }
}