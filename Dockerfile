FROM gradle:6.3.0-jdk11

COPY . /opt/the-hat

# run for downloading actual gradle from gradle-wrapper
RUN ./gradlew stage

WORKDIR /opt/the-hat/

RUN ./gradlew run