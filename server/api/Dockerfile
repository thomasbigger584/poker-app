FROM bellsoft/liberica-runtime-container:jdk-21-musl AS builder

RUN apk add --update g++ make git && \
    rm -rf /var/cache/apk/*

WORKDIR /app

RUN git clone -b develop https://github.com/HenryRLee/PokerHandEvaluator.git /app/src/main/cpp/PokerHandEvaluator
COPY ./src/main/cpp/ /app/src/main/cpp/

ENV EVALUATOR_SO_PATH=/app/src/main/cpp/evaluator.so
RUN g++ -shared -fPIC -Wl,--no-undefined -o $EVALUATOR_SO_PATH \
    /app/src/main/cpp/PokerEvaluator.cpp \
    /app/src/main/cpp/PokerHandEvaluator/cpp/src/evaluator7.c \
    /app/src/main/cpp/PokerHandEvaluator/cpp/src/hash.c \
    /app/src/main/cpp/PokerHandEvaluator/cpp/src/hashtable.c \
    /app/src/main/cpp/PokerHandEvaluator/cpp/src/hashtable7.c \
    /app/src/main/cpp/PokerHandEvaluator/cpp/src/dptables.c \
    /app/src/main/cpp/PokerHandEvaluator/cpp/src/tables_bitwise.c \
    -I /app/src/main/cpp/PokerHandEvaluator/cpp/include \
    -I $JAVA_HOME/include \
    -I $JAVA_HOME/include/linux

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN sed -i 's/\r//g' ./mvnw && chmod +x ./mvnw

COPY src ./src
RUN --mount=type=cache,target=~/.m2 ./mvnw --no-transfer-progress package

FROM bellsoft/liberica-runtime-container:jre-21-musl

WORKDIR /app

COPY --from=builder /app/target/api.jar /app/api.jar

ENV EVALUATOR_SO_PATH=/app/evaluator.so
COPY --from=builder /app/src/main/cpp/evaluator.so $EVALUATOR_SO_PATH

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "api.jar"]
