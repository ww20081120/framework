# Spring AI Ollama Demo

This demo shows how to use Spring AI with Ollama to interact with large language models.

## Model Used

This demo uses the `modelscope.cn/unsloth/Qwen3-Coder-30B-A3B-Instruct-GGUF:latest` model from ModelScope.

## Prerequisites

1. Java 17 or higher
2. Maven 3.6 or higher
3. Ollama installed and running
4. The Qwen3-Coder model pulled from ModelScope

## Setup

1. Install Ollama from [https://ollama.com/](https://ollama.com/)

2. Pull the model:
   ```bash
   ollama pull modelscope.cn/unsloth/Qwen3-Coder-30B-A3B-Instruct-GGUF:latest
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Alternative: Using the Build Script

You can also use the provided build script to automatically set up and run the application:

```bash
# Make the script executable (if not already)
chmod +x build.sh

# Run the build script
./build.sh
```

The build script will:
- Check for prerequisites (Java, Maven, Ollama)
- Pull the required model
- Build the project
- Start the application

## Endpoints

- `GET /ai` - Simple chat endpoint
- `GET /ai/code` - Code generation endpoint
- `GET /ai/structured` - Structured output endpoint

## Examples

```bash
# Simple chat
curl "http://localhost:8080/ai?message=Tell me a joke"

# Code generation
curl "http://localhost:8080/ai/code?description=Create a Java class for a Person with name and age"

# Structured output
curl "http://localhost:8080/ai/structured?description=Return a JSON object with name=John and age=30"
```

## Running Tests

To run the tests, you can use Maven:

```bash
# Run all tests
mvn test

# Run integration tests (requires Ollama to be running)
mvn verify
```

The project includes several types of tests:
1. Unit tests - Test individual components in isolation
2. Integration tests - Test the full application with real HTTP requests
3. Spring context tests - Verify that the Spring application context loads correctly

Note: Integration tests require Ollama to be running with the specified model. If Ollama is not running, those tests will fail.