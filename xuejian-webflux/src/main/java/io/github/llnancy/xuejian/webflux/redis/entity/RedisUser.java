package io.github.llnancy.xuejian.webflux.redis.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * redis user
 * must implement the {@link Serializable} interface
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Data
public class RedisUser implements Serializable {

    private static final long serialVersionUID = 7745741553417591187L;

    private String id;

    private String username;

    private String password;
}
