// package com.sunchaser.chunyu.json.config;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
// import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
// import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
// import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
// import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
// import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
// import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//
// import java.text.SimpleDateFormat;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.LocalTime;
// import java.time.format.DateTimeFormatter;
//
// /**
//  * MappingJackson2HttpMessageConverter 配置
//  *
//  * @author sunchaser admin@lilu.org.cn
//  * @since JDK8 2022/3/17
//  */
// @Configuration
// public class JacksonConfig {
//
//     public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//     public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//     public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
//
//     // @Bean
//     public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
//         MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//         ObjectMapper objectMapper = new ObjectMapper();
//         // 格式化Date
//         objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//
//         // 时间模块：格式化Java8的LocalDateTime
//         JavaTimeModule javaTimeModule = new JavaTimeModule();
//         javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
//         javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
//         javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER));
//         javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));
//         javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER));
//         javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));
//         objectMapper.registerModule(javaTimeModule);
//
//         converter.setObjectMapper(objectMapper);
//         return converter;
//     }
// }
