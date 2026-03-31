# 1. Java 22 기반 OpenJDK 이미지 사용
FROM eclipse-temurin:22-jdk-alpine

# 2. Gradle 빌드 후 생성된 JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 3. 컨테이너 실행 시 Spring Boot 앱 실행
ENTRYPOINT ["java","-jar","/app.jar"]

# 4. 기본 포트 지정 (Spring Boot 기본 8080)
EXPOSE 8080