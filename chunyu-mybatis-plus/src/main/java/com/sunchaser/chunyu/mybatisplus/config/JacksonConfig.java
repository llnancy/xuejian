package com.sunchaser.chunyu.mybatisplus.config;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 配置 Spring MVC 返回 json 格式时对 Java8 LocalDateTime 时间字段的格式化处理
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/13
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DatePattern.NORM_DATETIME_FORMATTER))
                .serializerByType(LocalDate.class, new LocalDateSerializer(DatePattern.NORM_DATE_FORMATTER))
                .serializerByType(LocalTime.class, new LocalTimeSerializer(DatePattern.NORM_TIME_FORMATTER))
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DatePattern.NORM_DATETIME_FORMATTER))
                .deserializerByType(LocalDate.class, new LocalDateDeserializer(DatePattern.NORM_DATE_FORMATTER))
                .deserializerByType(LocalTime.class, new LocalTimeDeserializer(DatePattern.NORM_TIME_FORMATTER));
    }
}
