package com.sunchaser.chunyu.graphql.kickstart.resolver.user.mutation;

import com.sunchaser.chunyu.graphql.kickstart.context.CustomGraphQLContext;
import com.sunchaser.chunyu.graphql.kickstart.model.User;
import com.sunchaser.chunyu.graphql.kickstart.model.input.CreateUserInput;
import com.sunchaser.chunyu.graphql.kickstart.publisher.UserPublisher;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * graphql user mutation resolver
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/11
 */
@Component
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserMutation implements GraphQLMutationResolver {

    private final UserPublisher publisher;

    public User createUser(@Valid CreateUserInput input, DataFetchingEnvironment environment) {
        log.info("Creating user. input: {}", input);

        // 请求中包含的需要查询的字段集合
        DataFetchingFieldSelectionSet selectionSet = environment.getSelectionSet();

        // 获取所有查询字段
        List<String> fieldNames = selectionSet.getFields()
                .stream()
                .map(SelectedField::getName)
                .collect(Collectors.toList());

        // 查询字段中是否包含id
        boolean containsId = selectionSet.contains("id");

        // 查询字段中是否同时包含id和name
        boolean containsAllOfIdAndName = selectionSet.containsAllOf("id", "name");

        // 查询字段中是否包含id或name（任意一个）
        boolean containsAnyOfIdAndName = selectionSet.containsAnyOf("id", "name");

        // 上下文
        // DefaultGraphQLServletContext context =  environment.getContext();
        CustomGraphQLContext context = environment.getContext();

        // Servlet API
        HttpServletRequest request = context.getHttpServletRequest();
        HttpServletResponse response = context.getHttpServletResponse();

        User user = User.builder()
                .id(UUID.randomUUID())
                .name(input.getName())
                .sex(input.getSex())
                .age(input.getAge())
                .createdOn(input.getCreatedOn().toLocalDate())
                .createdAt(input.getCreatedAt())
                .address(input.getAddress())
                .build();

        publisher.publish(user);
        return user;
    }
}
