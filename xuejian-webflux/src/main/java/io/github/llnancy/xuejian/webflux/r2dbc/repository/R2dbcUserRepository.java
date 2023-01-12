package io.github.llnancy.xuejian.webflux.r2dbc.repository;

import io.github.llnancy.xuejian.webflux.r2dbc.entity.R2dbcUser;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * r2dbc user repository
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
public interface R2dbcUserRepository extends R2dbcRepository<R2dbcUser, Long> {
}
