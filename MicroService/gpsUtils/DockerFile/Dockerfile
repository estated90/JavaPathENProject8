FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./jar/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]