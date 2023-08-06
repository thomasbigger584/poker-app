FROM ubuntu:latest

RUN apt-get -qq update && apt-get -qq install -y openjdk-17-jdk g++
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

WORKDIR /app
COPY . .

ENV EVALUATOR_SO_PATH=/app/src/main/cpp/evaluator.so
RUN g++ -shared -o $EVALUATOR_SO_PATH /app/src/main/cpp/* -I $JAVA_HOME/include -I $JAVA_HOME/include/linux

RUN ./mvnw --no-transfer-progress clean package -DskipTests

CMD ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","-Djava.security.egd=file:/dev/./urandom","-jar","target/api-0.0.1-SNAPSHOT.jar"]
