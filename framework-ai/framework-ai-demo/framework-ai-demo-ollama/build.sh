#!/bin/bash

# Spring AI Ollama Demo Build Script

echo "Spring AI Ollama Demo Build Script"
echo "=================================="

# Check if Java is installed
if ! command -v java &> /dev/null
then
    echo "Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null
then
    echo "Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

# Check if Ollama is installed
if ! command -v ollama &> /dev/null
then
    echo "Ollama is not installed. Please install Ollama from https://ollama.com/"
    exit 1
fi

echo "All prerequisites are installed."

# Pull the required model
echo "Pulling the Qwen3-Coder model..."
ollama pull modelscope.cn/unsloth/Qwen3-Coder-30B-A3B-Instruct-GGUF:latest

# Build the project
echo "Building the project..."
mvn clean install -pl framework-ai/framework-ai-demo-ollama

# Run the application
echo "Starting the application..."
mvn spring-boot:run -pl framework-ai/framework-ai-demo-ollama

echo "Application started. Access it at http://localhost:8080"