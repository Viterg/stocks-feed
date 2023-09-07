# Stocks Feed Service
## Training module for project Proselyte Advance

### About
This module represents high-load service for getting stocks by users authorized by JWT and api-key.
Requests are limited by rate limiter filter implementation. Users are stored in PostgreSQL, stocks are stored in Redis.

### Getting started
For project building run:
```  
./gradlew clean build 
```
### Additional Links
These additional references should also help you:
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Messaging with Redis](https://spring.io/guides/gs/messaging-redis/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

