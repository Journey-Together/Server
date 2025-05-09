# 1. 사용할 베이스 이미지 (amd64 플랫폼 명시)
FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine

# 2. 작업 디렉터리 생성
WORKDIR /app

# 3. 호스트의 build된 JAR 파일을 컨테이너에 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 4. 실행 포트 설정 (Nginx와 연동할 포트)
EXPOSE 8080

# 5. JAR 파일 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=release", "-jar", "app.jar"]