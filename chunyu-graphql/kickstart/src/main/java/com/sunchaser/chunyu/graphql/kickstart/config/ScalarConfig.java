package com.sunchaser.chunyu.graphql.kickstart.config;

import com.sunchaser.chunyu.graphql.kickstart.scalars.MyExtendedScalars;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Extend Scalar Configuration
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/16
 */
@Configuration
public class ScalarConfig {

    @Bean
    public GraphQLScalarType nonNegativeInt() {
        return ExtendedScalars.NonNegativeInt;
    }

    @Bean
    public GraphQLScalarType date() {
        return ExtendedScalars.Date;
    }

    @Bean
    public GraphQLScalarType dateTime() {
        return ExtendedScalars.DateTime;
    }

    @Bean
    public GraphQLScalarType localDateTime() {
        return MyExtendedScalars.LOCAL_DATE_TIME;
    }
}
