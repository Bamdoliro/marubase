FROM openjdk:17-jdk
FROM openjdk:17-jdk AS builder

COPY . /app

WORKDIR /app

RUN microdnf install findutils

RUN ./gradlew clean
RUN chmod +x ./gradlew
RUN ./gradlew bootJAR

FROM openjdk:17-jdk
COPY --from=builder app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/app.jar"]