package com.sunchaser.chunyu.graphql.kickstart.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * User.java -> user.graphqls
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/9
 */
@Value
@Builder
public class User {
    UUID id;
    String name;
    SexEnum sex;
    Integer age;
    Address address;
    User son;
    LocalDate createdOn;
    LocalDateTime createdAt;
}
