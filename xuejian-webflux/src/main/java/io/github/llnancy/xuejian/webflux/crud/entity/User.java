package io.github.llnancy.xuejian.webflux.crud.entity;

import lombok.Data;

/**
 * user entity
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Data
public class User {

    private Long id;

    private String username;

    private String password;
}
