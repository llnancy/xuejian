package io.github.llnancy.xuejian.webflux.r2dbc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * r2dbc user
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Table("user")
@Data
public class R2dbcUser {

    @Id
    private Long id;

    private String username;

    private String password;
}
