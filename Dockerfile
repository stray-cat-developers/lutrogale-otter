FROM eclipse-temurin:21-jdk-alpine as cache
ENV GRADLE_USER_HOME /gradle
RUN mkdir /gradle
RUN mkdir /app
WORKDIR /app
COPY gradlew build.gradle.kts gradle.properties ./
COPY gradle ./gradle
RUN ./gradlew dependencies

FROM eclipse-temurin:21-jdk-alpine as builder
COPY --from=cache /gradle /gradle
ENV GRADLE_USER_HOME /gradle
RUN mkdir /app
WORKDIR /app
COPY . .
RUN ./gradlew clean build

FROM eclipse-temurin:21-jdk-alpine as runner
COPY --from=builder /app/build/libs/lutrogale-1.0.2.jar lutrogale-1.0.2.jar
ENTRYPOINT [ "java", "-jar", "lutrogale-1.0.2.jar" ]