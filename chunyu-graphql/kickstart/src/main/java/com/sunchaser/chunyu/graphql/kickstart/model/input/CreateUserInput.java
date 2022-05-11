package com.sunchaser.chunyu.graphql.kickstart.model.input;

import com.sunchaser.chunyu.graphql.kickstart.model.Address;
import com.sunchaser.chunyu.graphql.kickstart.model.SexEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;

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
}
