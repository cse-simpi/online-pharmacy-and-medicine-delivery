FROM eclipse-temurin:25-jdk
VOLUME /tmp
# This looks into the 'target' folder you just created with mvnw
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]