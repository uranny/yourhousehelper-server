FROM eclipse-temurin:22-jdk

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]

EXPOSE 8080