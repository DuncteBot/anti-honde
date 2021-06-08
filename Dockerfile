FROM adoptopenjdk:16-jdk-hotspot AS builder

WORKDIR /honde
COPY gradle ./gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
RUN ./gradlew --no-daemon dependencies
COPY . .
RUN ./gradlew --no-daemon build

FROM adoptopenjdk:16-jre-hotspot

WORKDIR /honde
COPY --from=builder /honde/build/libs/anti-honde-*.jar ./honde.jar
CMD ["java", "-jar", "honde.jar"]