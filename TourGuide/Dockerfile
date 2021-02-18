FROM openjdk:8-jdk-alpine

LABEL maintener="nicolas.detesta90@gmail.com"

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=libs/TourGuide-1.0.0.jar

ADD ${JAR_FILE} tourGuide.jar

ENTRYPOINT ["java", "-jar", "/tourGuide.jar"] 