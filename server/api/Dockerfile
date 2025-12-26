FROM bellsoft/liberica-runtime-container:jdk-21-musl AS builder

# Install C++ build tools
RUN apk add --update g++ make && \
    rm -rf /var/cache/apk/*

WORKDIR /app

COPY . .

RUN sed -i 's/\r//g' ./mvnw && chmod +x ./mvnw

ENV EVALUATOR_SO_PATH=/app/src/main/cpp/evaluator.so
RUN g++ -shared -fPIC -o $EVALUATOR_SO_PATH /app/src/main/cpp/* \
    -I $JAVA_HOME/include \
    -I $JAVA_HOME/include/linux

RUN --mount=type=cache,target=~/.m2 ./mvnw --no-transfer-progress package

FROM bellsoft/liberica-runtime-container:jre-21-musl

WORKDIR /app

COPY --from=builder /app/target/api.jar /app/api.jar

ENV EVALUATOR_SO_PATH=/app/evaluator.so
COPY --from=builder /app/src/main/cpp/evaluator.so $EVALUATOR_SO_PATH

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "api.jar"]
