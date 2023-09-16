FROM amazoncorretto:17-alpine-jdk
COPY ./build/libs/* ./app.jar
ENTRYPOINT ["java","-jar","/app.jar"]