// package io.github.llnancy.xuejian.json.config;
//
// import com.google.gson.GsonBuilder;
// import com.google.gson.JsonPrimitive;
// import com.google.gson.JsonSerializer;
// import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
// import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.converter.json.GsonHttpMessageConverter;
//
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.LocalTime;
// import java.time.format.DateTimeFormatter;
// import java.util.List;
//
// /**
//  * {@link GsonBuilderCustomizer} 配置
//  *
//  * @author sunchaser admin@lilu.org.cn
//  * @since JDK8 2022/3/17
//  */
// @Configuration
// public class GsonConfig {
//
//     public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
//
//     public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
//
//     public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//     public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
//
//     /**
//      * {@link GsonAutoConfiguration#gsonBuilder(List)} 通过 {@link GsonBuilderCustomizer} 类来对 {@link com.google.gson.Gson} 对象进行自定义
//      * {@link GsonAutoConfiguration#gson(GsonBuilder)} 向 IOC 容器中注入 Gson 对象
//      * 通过以下方法将 IOC 容器中的 Gson 对象设置到新建的 {@link GsonHttpMessageConverter} 对象中并将其注入到 IOC 容器
//      * org.springframework.boot.autoconfigure.http.GsonHttpMessageConvertersConfiguration.GsonHttpMessageConverterConfiguration#gsonHttpMessageConverter(com.google.gson.Gson)
//      *
//      * @return {@link GsonBuilderCustomizer}
//      */
//     @Bean
//     public GsonBuilderCustomizer gsonBuilderCustomizer() {
//         return gsonBuilder -> gsonBuilder.registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DATE_TIME_FORMATTER)))
//                 .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DATE_FORMATTER)))
//                 .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(TIME_FORMATTER)))
//                 // 可用配置项 spring.gson.date-format=yyyy-MM-dd HH:mm:ss 代替此处的 setDateFormat 方法
//                 .setDateFormat(DATE_TIME_PATTERN);
//     }
// }
