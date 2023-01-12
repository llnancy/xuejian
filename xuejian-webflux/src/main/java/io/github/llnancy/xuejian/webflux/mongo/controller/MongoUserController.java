package io.github.llnancy.xuejian.webflux.mongo.controller;

import io.github.llnancy.xuejian.webflux.mongo.entity.MongoUser;
import io.github.llnancy.xuejian.webflux.mongo.repository.MongoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * mongo user controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/mongo")
public class MongoUserController {

    private final MongoUserRepository repository;

    @PostMapping("/user")
    public Mono<MongoUser> save(@RequestBody MongoUser user) {
        return repository.save(user);
    }

    @GetMapping("/user/{id}")
    public Mono<MongoUser> findById(@PathVariable String id) {
        return repository.findById(id);
    }

    @GetMapping("/users")
    public Flux<MongoUser> findAll() {
        return repository.findAll();
    }

    @DeleteMapping("/user/{id}")
    public Mono<Void> deleteById(@PathVariable String id) {
        return repository.deleteById(id);
    }
}
