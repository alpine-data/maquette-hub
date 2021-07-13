FROM adoptopenjdk/openjdk11:jdk-11.0.11_9-alpine AS build

WORKDIR /opt/app
COPY .mvn ./.mvn
COPY mvnw mvnw

# unfortunately we have to copy the POM-file for each module separately :(
COPY pom.xml pom.xml
COPY app/pom.xml ./app/pom.xml
COPY core/pom.xml ./core/pom.xml
COPY data-shop-api/pom.xml ./data-shop-api/pom.xml
COPY data-shop-impl/pom.xml ./data-shop-impl/pom.xml
COPY model-development-api/pom.xml ./model-development-api/pom.xml
COPY model-development-impl/pom.xml ./model-development-impl/pom.xml
COPY model-management-api/pom.xml ./model-management-api/pom.xml
COPY model-management-impl/pom.xml ./model-management-impl/pom.xml

# we do an maven install with empty projects to resolve all dependencies
# we cannot just use dependency:copy-dependency since dependencies between modules wouldn't resolve without install
RUN ./mvnw install

COPY . .
RUN ./mvnw clean install && \
    ./mvnw -pl app -am dependency:copy-dependencies

FROM adoptopenjdk/openjdk11:jdk-11.0.11_9-alpine

WORKDIR /opt/app
RUN apk add dumb-init

COPY --from=build /opt/app/app/target/dependency /opt/app
COPY --from=build /opt/app/app/target/*.jar /opt/app/app.jar

EXPOSE 9042
CMD "dumb-init" "java" "-jar" "app.jar"