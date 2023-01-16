package io.github.llnancy.xuejian.jpa.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * datasource1 config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/11
 */
@Configuration
// 开启事务管理器
@EnableTransactionManagement
@EnableJpaRepositories(
        // 指定 datasource1 的 repository 包扫描路径
        basePackages = {"io.github.llnancy.xuejian.jpa.db1.repository"},
        // 指定 datasource1 的 EntityManagerFactory
        entityManagerFactoryRef = "db1EntityManagerFactory",
        // 指定 datasource1 的 TransactionManager
        transactionManagerRef = "db1TransactionManager"
)
public class DataSource1Config {

    @Bean
    @Primary
    // 指定 datasource1 的配置项前缀
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    public DataSourceProperties db1DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    // 指定 datasource1 的 hikari 连接池配置项前缀
    @ConfigurationProperties(prefix = "spring.datasource.hikari.db1")
    public HikariDataSource db1DataSource(@Qualifier("db1DataSourceProperties") DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        String name = properties.getName();
        if (StringUtils.hasText(name)) {
            dataSource.setPoolName(name);
        }
        return dataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean db1EntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                          @Qualifier("db1DataSource") DataSource dataSource,
                                                                          HibernateProperties hibernateProperties,
                                                                          JpaProperties jpaProperties) {
        return builder.dataSource(dataSource)
                // 指定 datasource1 的实体类路径
                .packages("io.github.llnancy.xuejian.jpa.db1.entity")
                .persistenceUnit("db1")
                .properties(hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings()))
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager db1TransactionManager(@Qualifier("db1EntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }
}
