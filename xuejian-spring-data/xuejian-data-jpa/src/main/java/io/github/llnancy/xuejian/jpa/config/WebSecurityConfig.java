package io.github.llnancy.xuejian.jpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/11
 */
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeRequests()
                // 放行 h2-console 请求
                .antMatchers("/h2-console/**").permitAll()
                .and()
                // 禁用 h2-console 的 csrf 防护
                .csrf().ignoringAntMatchers("/h2-console/**")
                .and()
                // 允许 h2-console 使用 iframe
                .headers().frameOptions().sameOrigin()
                .and().build();
    }
}
