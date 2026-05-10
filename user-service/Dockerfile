FROM bellsoft/liberica-openjdk-alpine:17
EXPOSE 8081
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Xmx256m", "-jar", "/app.jar"]