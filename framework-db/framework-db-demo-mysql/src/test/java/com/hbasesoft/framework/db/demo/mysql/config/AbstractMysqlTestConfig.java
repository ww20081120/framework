package com.hbasesoft.framework.db.demo.mysql.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import com.hbasesoft.framework.common.Bootstrap;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.db.demo.mysql.Application;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(initializers = AbstractMysqlTestConfig.BootstrapInitializer.class)
@Testcontainers
public abstract class AbstractMysqlTestConfig {

    static final MySQLContainer MYSQL_CONTAINER;

    static {
        MYSQL_CONTAINER = new MySQLContainer("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");
        MYSQL_CONTAINER.start();

        // 使用 JDBC 直接执行 schema.sql 建表
        try (Connection conn = DriverManager.getConnection(
                MYSQL_CONTAINER.getJdbcUrl(),
                MYSQL_CONTAINER.getUsername(),
                MYSQL_CONTAINER.getPassword())) {
            ClassPathResource schemaResource = new ClassPathResource("schema.sql");
            ScriptUtils.executeSqlScript(conn, schemaResource);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute schema.sql", e);
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("master.db.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("master.db.username", MYSQL_CONTAINER::getUsername);
        registry.add("master.db.password", MYSQL_CONTAINER::getPassword);
    }

    public static class BootstrapInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(final ConfigurableApplicationContext applicationContext) {
            applicationContext.getEnvironment().getPropertySources().addFirst(new MapPropertySource("testcontainers",
                Map.of("master.db.url", MYSQL_CONTAINER.getJdbcUrl(), "master.db.username", MYSQL_CONTAINER.getUsername(),
                    "master.db.password", MYSQL_CONTAINER.getPassword())));
            ContextHolder.setContext(applicationContext);
            Bootstrap.before();
        }
    }
}
