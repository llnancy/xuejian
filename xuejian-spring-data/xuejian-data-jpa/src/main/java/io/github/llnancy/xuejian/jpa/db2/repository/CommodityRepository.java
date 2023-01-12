package io.github.llnancy.xuejian.jpa.db2.repository;

import io.github.llnancy.xuejian.jpa.db2.entity.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * commodity repository
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/11
 */
public interface CommodityRepository extends JpaRepository<Commodity, Long> {
}
