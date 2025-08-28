package com.hbasesoft.framework.ai.demo.ollama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Ollama demo.
 */
@SpringBootApplication
public class OllamaDemoApplication {

    /**
     * Main method to start the application.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(OllamaDemoApplication.class, args);
    }
}
