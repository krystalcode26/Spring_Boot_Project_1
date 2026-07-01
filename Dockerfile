# Stage 1: build JAR
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy pom.xml first so Docker can cache dependency downloads
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Copy source and build JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: run app
# Use a smaller base image for the final container
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy only the JAR file from the build stage
COPY --from=build /app/target/sms-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8088

# Command to run the JAR file when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
