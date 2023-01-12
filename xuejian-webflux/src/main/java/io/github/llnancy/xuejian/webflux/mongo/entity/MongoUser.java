package io.github.llnancy.xuejian.webflux.mongo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * mongo user
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@Data
@Document("user")
public class MongoUser {

    @Id
    private String id;

    private String username;

    private String password;
}
