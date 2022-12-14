// package io.github.llnancy.xuejian.json.config;
//
// import com.alibaba.fastjson.support.config.FastJsonConfig;
// import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.MediaType;
//
// import java.util.Arrays;
//
// /**
//  * {@link FastJsonHttpMessageConverter} 配置
//  *
//  * @author sunchaser admin@lilu.org.cn
//  * @since JDK8 2022/3/18
//  */
// @Configuration
// public class AlibabaFastJsonConfig {
//
//     @Bean
//     public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
//         FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
//         FastJsonConfig fastJsonConfig = converter.getFastJsonConfig();
//         // 格式化时间类型字段 Date 和 LocalDateTime
//         fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
//         // 添加支持的 MediaType 类型。由于 FastJsonHttpMessageConverter 的无参构造器中设置的 MediaType 类型为 ALL
//         // 会导致 org.springframework.http.HttpHeaders.setContentType 方法抛出 IllegalArgumentException 异常
//         converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED));
//         return converter;
//     }
// }
