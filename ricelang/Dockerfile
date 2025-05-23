FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src
COPY libs ./libs

RUN chmod +x gradlew

RUN ./gradlew bootJar

CMD ["java", "-jar", "build/libs/ricelang-0.0.1.jar"]
