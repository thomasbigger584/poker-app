FROM ubuntu:latest

# Install JDK 17 and g++
RUN apt-get update && apt-get install -y openjdk-17-jdk g++
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

WORKDIR /app
COPY . .

ENV EVALUATOR_SO_PATH=/app/src/main/cpp/evaluator.so

RUN g++ -shared -o $EVALUATOR_SO_PATH /app/src/main/cpp/* -I $JAVA_HOME/include -I $JAVA_HOME/include/linux
RUN ./mvnw package

CMD ["java", "-jar", "target/poker-game-backend-0.0.1-SNAPSHOT.jar"]
