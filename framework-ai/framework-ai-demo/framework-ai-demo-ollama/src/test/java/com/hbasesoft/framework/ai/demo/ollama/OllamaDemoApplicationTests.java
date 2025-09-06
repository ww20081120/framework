package com.hbasesoft.framework.ai.demo.ollama;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
class OllamaDemoApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the application context loads successfully
        // with all the beans properly configured
    }

}
