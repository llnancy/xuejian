package io.github.llnancy.xuejian.webflux.mongo.repository;

import io.github.llnancy.xuejian.webflux.mongo.entity.MongoUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * mongo user repository
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Repository
public interface MongoUserRepository extends ReactiveMongoRepository<MongoUser, String> {
}
