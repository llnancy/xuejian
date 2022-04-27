package com.sunchaser.chunyu.apollo.config;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * apollo 配置更新监听器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/28
 */
@Configuration
@Slf4j
public class ApolloRefresherConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @ApolloConfigChangeListener(value = {"application", "application.yml"})
    private void refreshConfig(ConfigChangeEvent configChangeEvent) {
        log.info("listened config change event, the namespace is {}", configChangeEvent.getNamespace());
        Set<String> changedKeys = configChangeEvent.changedKeys();
        for (String changedKey : changedKeys) {
            ConfigChange change = configChangeEvent.getChange(changedKey);
            log.info("Found change - key: {}, oldValue: {}, newValue: {}, changeType: {}", change.getPropertyName(), change.getOldValue(), change.getNewValue(), change.getChangeType());
        }
        // 更新@ConfigurationProperties注解注入的属性
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changedKeys));
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
