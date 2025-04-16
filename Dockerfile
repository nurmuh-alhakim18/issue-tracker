FROM openjdk:21-jdk-slim

WORKDIR /app

COPY .mvn .mvn
COPY src src
COPY mvnw pom.xml ./

RUN chmod +x mvnw && ./mvnw clean package -DskipTests

RUN cp target/*.jar app.jar

EXPOSE 8000

CMD ["java", "-jar", "app.jar"]