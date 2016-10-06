FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/issues-combined.jar /issues-combined/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/issues-combined/app.jar"]
