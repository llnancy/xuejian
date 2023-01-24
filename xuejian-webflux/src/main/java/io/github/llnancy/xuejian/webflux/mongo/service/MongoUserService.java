package io.github.llnancy.xuejian.webflux.mongo.service;

import io.github.llnancy.xuejian.webflux.mongo.entity.MongoUser;
import io.github.llnancy.xuejian.webflux.mongo.repository.MongoUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link MongoUser} service
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/24
 */
@Service
@AllArgsConstructor
public class MongoUserService {

    private final MongoUserRepository repository;

    public Mono<MongoUser> save(@RequestBody MongoUser user) {
        return repository.save(user);
    }

    public Mono<MongoUser> findById(@PathVariable String id) {
        return repository.findById(id);
    }

    public Flux<MongoUser> findAll() {
        return repository.findAll();
    }

    public Mono<Void> deleteById(@PathVariable String id) {
        return repository.deleteById(id);
    }
}
