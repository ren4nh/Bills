FROM openjdk:8-jdk-alpine as build
ENV LANG=pt_BR.UTF-8
RUN apk add --no-cache maven
WORKDIR /java
COPY . /java
RUN mvn clean package
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/java/target/bills-0.0.1-SNAPSHOT.jar"]