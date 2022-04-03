FROM openjdk:17.0.2-jdk

ARG JAR_FILE=build/libs/GithubPlantUMLer-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]