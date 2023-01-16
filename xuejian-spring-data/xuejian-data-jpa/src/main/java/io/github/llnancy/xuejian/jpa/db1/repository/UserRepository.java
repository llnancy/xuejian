package io.github.llnancy.xuejian.jpa.db1.repository;

import io.github.llnancy.xuejian.jpa.db1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * user repository
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/11
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
