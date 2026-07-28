FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /workspace

COPY pom.xml .
COPY src ./src

CMD ["mvn", "test"]
