package io.github.llnancy.xuejian.graphql.kickstart.scalars.datetime;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import static graphql.scalars.util.Kit.typeName;

/**
 * LocalDateTime Scalar
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/18
 */
public class LocalDateTimeScalar {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final GraphQLScalarType INSTANCE;

    private LocalDateTimeScalar() {
    }

    static {
        INSTANCE = GraphQLScalarType.newScalar()
                .name("LocalDateTime")
                .description("An implementation of Java8 LocalDateTime Scalar")
                .coercing(new Coercing<LocalDateTime, Object>() {

                    @Override
                    public Object serialize(Object dataFetcherResult) throws CoercingSerializeException {
                        return serializeLocalDateTime(dataFetcherResult);
                    }

                    @Override
                    public LocalDateTime parseValue(Object input) throws CoercingParseValueException {
                        return parseLocalDateTimeFromVariable(input);
                    }

                    @Override
                    public LocalDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
                        return parseLocalDateTimeFromAstLiteral(input);
                    }
                })
                .build();
    }

    private static Object serializeLocalDateTime(Object dataFetcherResult) {
        LocalDateTime localDateTime;
        if (dataFetcherResult instanceof LocalDateTime) {
            localDateTime = (LocalDateTime) dataFetcherResult;
        } else {
            throw new CoercingSerializeException(
                    "Expected something we can convert to 'java.time.LocalDateTime' but was '" + typeName(dataFetcherResult) + "'."
            );
        }
        try {
            return FORMATTER.format(localDateTime);
        } catch (Exception e) {
            throw new CoercingSerializeException(
                    "Unable to turn TemporalAccessor into LocalDateTime because of : '" + e.getMessage() + "'."
            );
        }
    }

    private static LocalDateTime parseLocalDateTimeFromVariable(Object input) {
        LocalDateTime localDateTime;
        if (input instanceof LocalDateTime) {
            localDateTime = (LocalDateTime) input;
        } else if (input instanceof String) {
            localDateTime = parseLocalDateTime(input.toString(), CoercingParseValueException::new);
        } else {
            throw new CoercingParseValueException(
                    "Expected a 'String' but was '" + typeName(input) + "'."
            );
        }
        return localDateTime;
    }

    private static LocalDateTime parseLocalDateTimeFromAstLiteral(Object input) {
        if (!(input instanceof StringValue)) {
            throw new CoercingParseLiteralException(
                    "Expected AST type 'StringValue' but was '" + typeName(input) + "'."
            );
        }
        return parseLocalDateTime(((StringValue) input).getValue(), CoercingParseLiteralException::new);
    }

    private static LocalDateTime parseLocalDateTime(String s, Function<String, RuntimeException> exceptionMaker) {
        try {
            return LocalDateTime.parse(s, FORMATTER);
        } catch (Exception e) {
            throw exceptionMaker.apply("Invalid LocalDateTime value : '" + s + "'. because of : '" + e.getMessage() + "'");
        }
    }
}
