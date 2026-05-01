FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -B
RUN rm target/*-plain.jar || true
RUN cp target/*.jar app.jar

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /app/app.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
