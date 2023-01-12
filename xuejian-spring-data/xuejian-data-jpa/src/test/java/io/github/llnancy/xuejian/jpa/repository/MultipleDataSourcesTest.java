package io.github.llnancy.xuejian.jpa.repository;

import io.github.llnancy.xuejian.jpa.db1.entity.User;
import io.github.llnancy.xuejian.jpa.db1.repository.UserRepository;
import io.github.llnancy.xuejian.jpa.db2.entity.Commodity;
import io.github.llnancy.xuejian.jpa.db2.repository.CommodityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * test multiple datasource
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/11
 */
@SpringBootTest
public class MultipleDataSourcesTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommodityRepository commodityRepository;

    @MockBean
    private AuditorAware<String> auditorAware;

    @BeforeEach
    public void before() {
        Mockito.when(auditorAware.getCurrentAuditor())
                .thenReturn(Optional.of("jpa-multi-datasource"));
    }

    @Test
    public void test() {
        User user = new User();
        user.setUsername("datasource1");
        userRepository.save(user);

        Commodity commodity = new Commodity();
        commodity.setName("datasource2");
        commodityRepository.save(commodity);
    }
}
