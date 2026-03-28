FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
# This looks into the 'target' folder you just created with mvnw
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]