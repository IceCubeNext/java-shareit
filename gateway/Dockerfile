FROM amazoncorretto:11
COPY target/*.jar app.jar
#ENV TZ="Europe/Moscow"
ENTRYPOINT ["java", "-jar", "/app.jar"]