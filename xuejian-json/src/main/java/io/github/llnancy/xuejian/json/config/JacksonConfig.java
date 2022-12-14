package io.github.llnancy.xuejian.json.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * {@link Jackson2ObjectMapperBuilderCustomizer} 配置
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/17
 */
@Configuration
public class JacksonConfig {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * {@link JacksonAutoConfiguration} JacksonObjectMapperBuilderConfiguration#jacksonObjectMapperBuilder
     * 通过 {@link Jackson2ObjectMapperBuilderCustomizer} 类来对 {@link com.fasterxml.jackson.databind.ObjectMapper} 对象进行自定义
     * 通过以下方法向 IOC 容器中注入 ObjectMapper 对象：
     * org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.JacksonObjectMapperConfiguration#jacksonObjectMapper(org.springframework.http.converter.json.Jackson2ObjectMapperBuilder)
     * 通过以下方法将 IOC 容器中的 ObjectMapper 对象设置到新建的 {@link MappingJackson2HttpMessageConverter} 对象中并将其注入到 IOC 容器：
     * org.springframework.boot.autoconfigure.http.JacksonHttpMessageConvertersConfiguration.MappingJackson2HttpMessageConverterConfiguration#mappingJackson2HttpMessageConverter(com.fasterxml.jackson.databind.ObjectMapper)
     *
     * @return {@link Jackson2ObjectMapperBuilderCustomizer}
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER))
                .serializerByType(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER))
                .serializerByType(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER))
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER))
                .deserializerByType(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER))
                .deserializerByType(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER))
                // 可用配置项 spring.jackson.date-format=yyyy-MM-dd HH:mm:ss 代替此处的 dateFormat 方法
                .dateFormat(new SimpleDateFormat(DATE_TIME_PATTERN));
    }
}
