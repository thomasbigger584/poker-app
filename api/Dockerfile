# First stage: Build stage with JDK
FROM eclipse-temurin:21-jdk-alpine as builder

# Install necessary build tools
RUN apk add g++ && \
    rm -rf /var/cache/apk/*

# Set working directory
WORKDIR /app

# Copy source code
COPY . .

# Compile C++ code with JDK
ENV EVALUATOR_SO_PATH=/app/src/main/cpp/evaluator.so
RUN g++ -shared -o $EVALUATOR_SO_PATH /app/src/main/cpp/* -I $JAVA_HOME/include -I $JAVA_HOME/include/linux

# Build Java application
RUN --mount=type=cache,target=~/.m2  ./mvnw --no-transfer-progress package -DskipTests

# Second stage: Runtime stage with JRE
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy compiled Java code from the builder stage
COPY --from=builder /app/target/api-0.0.1-SNAPSHOT.jar /app/

# Copy compiled C++ files from the builder stage to the JRE stage
ENV EVALUATOR_SO_PATH=/app/evaluator.so
COPY --from=builder /app/src/main/cpp/evaluator.so $EVALUATOR_SO_PATH

# Define command to run the application
CMD ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","-Djava.security.egd=file:/dev/./urandom","-jar","api-0.0.1-SNAPSHOT.jar"]
