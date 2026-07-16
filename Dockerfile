FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM ghcr.io/oezcan-guezelsarslan/javaimage:v1.2.0

# Prepare directories
RUN mkdir -p /opt/application /opt/application-conf /opt/application-log

COPY --from=build /app/target/application.jar /opt/application/


ENV JAVA_OPTS=""

# Exec form entrypoint for safe OS signal handling
ENTRYPOINT ["sh", "-c", "exec java -XX:+PerfDisableSharedMem $JAVA_OPTS -Dloader.path=/opt/application-conf,/opt/application/application.jar -jar /opt/application/application.jar"]