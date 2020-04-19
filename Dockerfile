FROM gradle:6.3.0-jdk11

WORKDIR /opt/the-hat/

COPY gradlew /opt/the-hat/gradlew
RUN ./gradlew tasks

COPY . /opt/the-hat
# run for downloading actual gradle from gradle-wrapper
RUN ./gradlew clean build

CMD java -jar build/libs/the-hat-1.0.jar
