// package com.sunchaser.chunyu.json.config;
//
// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;
// import com.google.gson.JsonPrimitive;
// import com.google.gson.JsonSerializer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.converter.json.GsonHttpMessageConverter;
//
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.LocalTime;
// import java.time.format.DateTimeFormatter;
//
// /**
//  * GsonHttpMessageConverter 配置
//  *
//  * @author sunchaser admin@lilu.org.cn
//  * @since JDK8 2022/3/17
//  */
// @Configuration
// public class GsonConfig {
//
//     @Bean
//     public GsonHttpMessageConverter gsonHttpMessageConverter() {
//         GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
//         GsonBuilder builder = new GsonBuilder();
//         // 格式化Date
//         builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
//         // 格式化LocalDateTime
//         builder.registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> {
//             String format = src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//             return new JsonPrimitive(format);
//         });
//         builder.registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> {
//             String format = src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//             return new JsonPrimitive(format);
//         });
//         builder.registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) -> {
//             String format = src.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
//             return new JsonPrimitive(format);
//         });
//         Gson gson = builder.create();
//         converter.setGson(gson);
//         return converter;
//     }
// }
