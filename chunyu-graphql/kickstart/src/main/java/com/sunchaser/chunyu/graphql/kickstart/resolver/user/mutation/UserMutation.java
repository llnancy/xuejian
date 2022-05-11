package com.sunchaser.chunyu.graphql.kickstart.resolver.user.mutation;

import com.sunchaser.chunyu.graphql.kickstart.model.User;
import com.sunchaser.chunyu.graphql.kickstart.model.input.CreateUserInput;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.UUID;

/**
 * graphql user mutation resolver
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/11
 */
@Component
@Slf4j
@Validated
public class UserMutation implements GraphQLMutationResolver {

    public User createUser(@Valid CreateUserInput input) {
        log.info("Creating user. input: {}", input);

        return User.builder()
                .id(UUID.randomUUID())
                .name(input.getName())
                .sex(input.getSex())
                .age(input.getAge())
                .address(input.getAddress())
                .build();
    }
}
