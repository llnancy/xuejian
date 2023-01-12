package io.github.llnancy.xuejian.webflux.redis.controller;

import io.github.llnancy.xuejian.webflux.redis.entity.RedisUser;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * reactive redis
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@RestController
@RequestMapping("/redis")
public class ReactiveRedisController {

    private static final String PREFIX = "user_";

    /**
     * 如果此处指定了泛型，则需要使用 @Resource 注解按 bean 名称注入。
     */
    @Resource
    private ReactiveRedisTemplate<String, RedisUser> reactiveRedisTemplate;

    @PostMapping("/set")
    public Mono<RedisUser> set(@RequestBody RedisUser user) {
        String key = PREFIX + user.getId();
        return reactiveRedisTemplate.opsForValue().getAndSet(key, user);
    }

    @GetMapping("/get/{id}")
    public Mono<RedisUser> get(@PathVariable Long id) {
        return reactiveRedisTemplate.opsForValue().get(PREFIX + id);
    }

    @DeleteMapping("/del/{id}")
    public Mono<Long> del(@PathVariable Long id) {
        return reactiveRedisTemplate.delete(PREFIX + id);
    }
}
