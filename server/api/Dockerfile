FROM bellsoft/liberica-runtime-container:jdk-21-musl AS builder

# Install C++ build tools
RUN apk add --update g++ make git && \
    rm -rf /var/cache/apk/*

WORKDIR /app

COPY . .

RUN sed -i 's/\r//g' ./mvnw && chmod +x ./mvnw

# Clone the poker hand evaluator repository
RUN git clone -b develop https://github.com/HenryRLee/PokerHandEvaluator.git /app/src/main/cpp/PokerHandEvaluator

ENV EVALUATOR_SO_PATH=/app/src/main/cpp/evaluator.so

# Compile the shared library using the cloned repository
# The symbols bit_of_mod_4_x_3 and bit_of_div_4 are missing.
# They are likely defined in tables_bitwise.c
# I saw tables_bitwise.c in the file list earlier: /app/src/main/cpp/PokerHandEvaluator/cpp/src/tables_bitwise.c
#
# So we need to include tables_bitwise.c as well.

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

RUN --mount=type=cache,target=~/.m2 ./mvnw --no-transfer-progress package

FROM bellsoft/liberica-runtime-container:jre-21-musl

WORKDIR /app

COPY --from=builder /app/target/api.jar /app/api.jar

ENV EVALUATOR_SO_PATH=/app/evaluator.so
COPY --from=builder /app/src/main/cpp/evaluator.so $EVALUATOR_SO_PATH

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "api.jar"]
