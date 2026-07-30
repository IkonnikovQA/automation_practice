FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /workspace

COPY pom.xml .
COPY test-core ./test-core
COPY api-tests ./api-tests
COPY ui-tests ./ui-tests
COPY hybrid-tests ./hybrid-tests

CMD ["mvn", "test"]
