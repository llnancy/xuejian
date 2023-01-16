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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * datasource2 config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/11
 */
@Configuration
// 开启事务管理器
@EnableTransactionManagement
@EnableJpaRepositories(
        // 指定 datasource2 的 repository 包扫描路径
        basePackages = {"io.github.llnancy.xuejian.jpa.db2.repository"},
        // 指定 datasource2 的 EntityManagerFactory
        entityManagerFactoryRef = "db2EntityManagerFactory",
        // 指定 datasource2 的 TransactionManager
        transactionManagerRef = "db2TransactionManager"
)
public class DataSource2Config {

    @Bean
    // 指定 datasource2 的配置项前缀
    @ConfigurationProperties(prefix = "spring.datasource.db2")
    public DataSourceProperties db2DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    // 指定 datasource2 的 hikari 连接池配置项前缀
    @ConfigurationProperties(prefix = "spring.datasource.hikari.db2")
    public HikariDataSource db2DataSource(@Qualifier("db2DataSourceProperties") DataSourceProperties properties) {
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
    public LocalContainerEntityManagerFactoryBean db2EntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                          @Qualifier("db2DataSource") DataSource dataSource,
                                                                          HibernateProperties hibernateProperties,
                                                                          JpaProperties jpaProperties) {
        return builder.dataSource(dataSource)
                // 指定 datasource2 的实体类路径
                .packages("io.github.llnancy.xuejian.jpa.db2.entity")
                .persistenceUnit("db2")
                .properties(hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings()))
                .build();
    }

    @Bean
    public PlatformTransactionManager db2TransactionManager(@Qualifier("db2EntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }
}
