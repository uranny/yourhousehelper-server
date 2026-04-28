# 1. Java 22 기반 이미지
FROM eclipse-temurin:22-jdk-alpine

# 2. JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 3. JVM 옵션 적용해서 실행 (핵심)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]

# 4. 포트
EXPOSE 8080