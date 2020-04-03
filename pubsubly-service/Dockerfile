#version 1.1
FROM java:8
VOLUME /tmp
ADD /target/pubsubly-service.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar","/app.jar"]