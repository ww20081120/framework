package com.hbasesoft.framework.db.demo.mysql.config;

import com.hbasesoft.framework.db.demo.mysql.Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
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
}
