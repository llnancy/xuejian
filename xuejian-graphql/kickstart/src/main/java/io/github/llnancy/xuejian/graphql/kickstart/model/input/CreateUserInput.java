package io.github.llnancy.xuejian.graphql.kickstart.model.input;

import io.github.llnancy.xuejian.graphql.kickstart.model.Address;
import io.github.llnancy.xuejian.graphql.kickstart.model.SexEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * CreateUserInput.java -> createUserInput.graphqls
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/11
 */
@Data
public class CreateUserInput {
    @NotBlank
    private String name;
    private SexEnum sex;
    private Integer age;
    private Address address;
    LocalDateTime createdOn;
    LocalDateTime createdAt;
}
