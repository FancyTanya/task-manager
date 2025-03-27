package org.bitbucket.privattaskmanager.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bitbucket.privattaskmanager.datasource.DataSourceContextHolder;
import org.bitbucket.privattaskmanager.datasource.RoutingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Configuration
@PropertySource("classpath:application.properties")
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);


    //    @Bean(name = "mainDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource mainDataSource() {
//        return org.springframework.boot.jdbc.DataSourceBuilder.create().build();
//    }
    @Bean
    @Qualifier("mainDataSource")
    public DataSource mainDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        config.setDriverClassName("org.h2.Driver");
        config.setUsername("sa");
        config.setPassword("");
        return new HikariDataSource(config);
    }


    //    @Bean(name = "backupDataSource")
//    public DataSource backupDataSource(
//            @Value("${spring.backup.datasource.url}") String url,
//            @Value("${spring.backup.datasource.username}") String username,
//            @Value("${spring.backup.datasource.password}") String password,
//            @Value("${spring.backup.datasource.driver-class-name}") String driverClassName) {
//
//        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
//        dataSourceBuilder.url(url);
//        dataSourceBuilder.username(username);
//        dataSourceBuilder.password(password);
//        dataSourceBuilder.driverClassName(driverClassName);
//        dataSourceBuilder.type(HikariDataSource.class);
//
//        return dataSourceBuilder.build();
//    }
    @Bean
    @Qualifier("backupDataSource")
    public DataSource backupDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/task_manager");
        config.setDriverClassName("org.postgresql.Driver");
        config.setUsername("postgres");
        config.setPassword("123456");
        return new HikariDataSource(config);
    }

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("mainDataSource") DataSource mainDataSource,
                                 @Qualifier("backupDataSource") DataSource backupDataSource) {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("MAIN", mainDataSource);
        dataSourceMap.put("BACKUP", backupDataSource);
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(mainDataSource);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    public static void switchToBackup() {
        logger.info("Switching to backup data source (PostgreSQL)");
        DataSourceContextHolder.setDataSourceKey("BACKUP");
    }

    public static void switchToMain() {
        logger.info("Switching to main data source (H2)");
        DataSourceContextHolder.setDataSourceKey("MAIN");
    }

}

