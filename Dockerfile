FROM openjdk:11-jdk-slim

WORKDIR /opt/the-hat/

COPY *.gradle gradle.* gradlew /opt/the-hat/
COPY gradle /opt/the-hat/gradle
RUN ./gradlew --version

COPY . .
# run for downloading actual gradle from gradle-wrapper
RUN ./gradlew --no-daemon clean build

CMD java -jar build/libs/the-hat-1.0.jar
