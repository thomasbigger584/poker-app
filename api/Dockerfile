FROM alpine:latest

RUN apk --no-cache add openjdk17 g++ && \
    rm -rf /var/cache/apk/*

ENV JAVA_HOME=/usr/lib/jvm/default-jvm

WORKDIR /app
COPY . .

ENV EVALUATOR_SO_PATH=/app/src/main/cpp/evaluator.so
RUN g++ -shared -o $EVALUATOR_SO_PATH /app/src/main/cpp/* -I $JAVA_HOME/include -I $JAVA_HOME/include/linux

RUN --mount=type=cache,target=~/.m2 ./mvnw --no-transfer-progress package -DskipTests

CMD ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","-Djava.security.egd=file:/dev/./urandom","-jar","target/api-0.0.1-SNAPSHOT.jar"]
