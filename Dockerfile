FROM openjdk:8-jre-alpine
VOLUME /tmp
ARG JAR_FILE=build/libs/coupon-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
HEALTHCHECK --interval=7s --timeout=3s --retries=3 CMD wget --spider http://localhost:8080/actuator/health || exit 1