package io.github.llnancy.xuejian.graphql.kickstart.scalars;

import io.github.llnancy.xuejian.graphql.kickstart.scalars.datetime.LocalDateTimeScalar;
import graphql.schema.GraphQLScalarType;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/18
 */
public class MyExtendedScalars {
    public static final GraphQLScalarType LOCAL_DATE_TIME = LocalDateTimeScalar.INSTANCE;
}
