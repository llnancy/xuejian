package io.github.llnancy.xuejian.webflux.redis.config;

import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Configuration
public class RedisConfiguration {

    /**
     * reactiveRedisTemplate serialize config
     *
     * @param connectionFactory {@link ReactiveRedisConnectionFactory}
     * @return {@link ReactiveRedisTemplate}
     * @see RedisReactiveAutoConfiguration#reactiveRedisTemplate(ReactiveRedisConnectionFactory, ResourceLoader)
     */
    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext.<String, Object>newSerializationContext()
                .key(StringRedisSerializer.UTF_8)
                .value(RedisSerializer.json())
                .hashKey(StringRedisSerializer.UTF_8)
                .hashValue(RedisSerializer.json())
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
}
