package io.github.llnancy.xuejian.webflux.crud.controller;

import io.github.llnancy.xuejian.webflux.crud.entity.User;
import io.github.llnancy.xuejian.webflux.crud.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * user controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    public Mono<Long> save(@RequestBody User user) {
        return userService.save(user);
    }

    @GetMapping("/user/{id}")
    public Mono<User> findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/users")
    public Flux<User> findAll() {
        return userService.findAll();
    }

    @DeleteMapping("/user/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return userService.deleteById(id);
    }
}
