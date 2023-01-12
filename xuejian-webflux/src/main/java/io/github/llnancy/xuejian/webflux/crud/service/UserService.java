package io.github.llnancy.xuejian.webflux.crud.service;

import io.github.llnancy.xuejian.webflux.crud.entity.User;
import io.github.llnancy.xuejian.webflux.crud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * user service
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<Long> save(User user) {
        return Mono.create(sink -> sink.success(userRepository.save(user)));
    }

    public Mono<User> findById(Long id) {
        return Mono.justOrEmpty(userRepository.findById(id));
    }

    public Flux<User> findAll() {
        return Flux.fromIterable(userRepository.findAll());
    }

    public Mono<Void> deleteById(Long id) {
        return Mono.create(sink -> {
            userRepository.deleteById(id);
            sink.success();
        });
    }
}
