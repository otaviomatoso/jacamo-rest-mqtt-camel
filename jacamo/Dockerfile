# JaCaMo-REST: Integration Demo (Camel + MQTT)

# to build:
#    ./gradlew clean copyToLib
#    docker build -t <image-name> .
# to run
#    docker run -it <image-name>

FROM alpine

ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk
ENV PATH $PATH:$JAVA_HOME/bin

RUN apk add --update --no-cache openjdk8-jre bash

COPY . /app/

EXPOSE 3271
EXPOSE 3272
EXPOSE 3273
EXPOSE 8080
EXPOSE 8081

WORKDIR /app

CMD ["java", "-cp", "build/classes/java/main:build/libs/*","jacamo.infra.JaCaMoLauncher", "src/jcm/main.jcm"]
