package io.github.llnancy.xuejian.webflux.r2dbc.controller;

import io.github.llnancy.xuejian.webflux.r2dbc.entity.R2dbcUser;
import io.github.llnancy.xuejian.webflux.r2dbc.repository.R2dbcUserRepository;
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
 * r2dbc user controller
 * JDK version can not be too high, we recommend using JDK8.
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/r2dbc")
public class R2dbcUserController {

    private final R2dbcUserRepository repository;

    @PostMapping("/user")
    public Mono<R2dbcUser> save(@RequestBody R2dbcUser user) {
        return repository.save(user);
    }

    @GetMapping("/user/{id}")
    public Mono<R2dbcUser> findById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping("/users")
    public Flux<R2dbcUser> findAll() {
        return repository.findAll();
    }

    @DeleteMapping("/user/{id}")
    public Mono<Void> deleteById(@PathVariable Long id) {
        return repository.deleteById(id);
    }
}
