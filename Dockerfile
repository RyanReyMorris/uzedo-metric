FROM openjdk:17
LABEL authors="ryanr"
WORKDIR /uzedo-metric
COPY target/uzedo-metric-0.0.1-SNAPSHOT.jar .
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "uzedo-metric-0.0.1-SNAPSHOT.jar"]