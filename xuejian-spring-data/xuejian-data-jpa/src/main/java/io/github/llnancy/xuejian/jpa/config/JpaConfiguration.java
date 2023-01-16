package io.github.llnancy.xuejian.jpa.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Objects;
import java.util.Optional;

/**
 * JPA Configuration
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/10
 */
@Configuration
@EnableJpaAuditing
public class JpaConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuditorAware<String> auditorAware() {
        // 以 Spring Security 为例
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
                return Optional.empty();
            }
            return Optional.ofNullable(((User) authentication.getPrincipal()).getUsername());
        };
    }
}
