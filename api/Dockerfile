# First stage: Build stage with JDK
FROM openjdk:17 as builder

# Install necessary build tools
RUN apt-get update && apt-get install -y g++

# Set working directory
WORKDIR /app

# Copy source code
COPY . .

# Compile C++ code with JDK
ENV JAVA_HOME=/usr/local/openjdk-17
ENV EVALUATOR_SO_PATH=/app/src/main/cpp/evaluator.so
RUN g++ -shared -o $EVALUATOR_SO_PATH /app/src/main/cpp/* -I $JAVA_HOME/include -I $JAVA_HOME/include/linux

# Build Java application
RUN --mount=type=cache,target=~/.m2  ./mvnw --no-transfer-progress package -DskipTests

# Second stage: Runtime stage with JRE
FROM openjdk:17-jre-slim

# Set working directory
WORKDIR /app

# Set JAVA_HOME to the JRE path
ENV JAVA_HOME=/usr/local/openjdk-17/jre

# Copy compiled Java code from the builder stage
COPY --from=builder /app/target/api-0.0.1-SNAPSHOT.jar /app/

# Copy compiled C++ files from the builder stage to the JRE stage
COPY --from=builder /app/src/main/cpp/evaluator.so /app/src/main/cpp/evaluator.so

# Define command to run the application
CMD ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","-Djava.security.egd=file:/dev/./urandom","-jar","api-0.0.1-SNAPSHOT.jar"]
