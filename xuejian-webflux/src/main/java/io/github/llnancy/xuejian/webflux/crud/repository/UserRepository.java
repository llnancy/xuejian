package io.github.llnancy.xuejian.webflux.crud.repository;

import io.github.llnancy.xuejian.webflux.crud.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * user repository
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Repository
public class UserRepository {

    private final ConcurrentMap<Long, User> repository = new ConcurrentHashMap<>();

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    public Long save(User user) {
        long id = ID_GENERATOR.incrementAndGet();
        user.setId(id);
        repository.put(id, user);
        return id;
    }

    public Collection<User> findAll() {
        return repository.values();
    }

    public User findById(Long id) {
        return repository.get(id);
    }

    public void deleteById(Long id) {
        repository.remove(id);
    }
}
