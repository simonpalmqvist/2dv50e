FROM openjdk:8-jre-alpine

COPY target/investigitor.jar /app.jar
COPY ./wait-for-it.sh /usr/local/bin/wait-for-it.sh
RUN chmod +x /usr/local/bin/wait-for-it.sh
RUN apk update
RUN apk upgrade
RUN apk add bash
RUN apk add git

ENTRYPOINT ["/usr/local/bin/wait-for-it.sh", "db:5432", "--", "/usr/bin/java", "-Xmx6000m", "-jar", "/app.jar"]