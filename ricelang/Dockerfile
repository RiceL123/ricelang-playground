# Use Eclipse Temurin (OpenJDK) base image
FROM eclipse-temurin:17-jdk

# Create app directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle

# Copy source files
COPY src ./src
COPY libs ./libs  # if you’re using jasmin.jar here

# Grant permission to gradlew
RUN chmod +x gradlew

# Build the application
RUN ./gradlew bootJar

# Run the Spring Boot app
CMD ["java", "-jar", "build/libs/ricelang-0.0.1.jar"]
