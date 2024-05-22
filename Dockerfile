FROM amd64/amazoncorretto:17

WORKDIR /app

COPY ./build/libs/Together-0.0.1-SNAPSHOT.jar /app/together.jar

CMD ["java", "-Duser.timezone=Asia/Seoul", "-jar", "together.jar"]
