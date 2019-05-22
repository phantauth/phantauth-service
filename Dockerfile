FROM gradle:jdk10 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar

FROM openjdk:10-jre-slim
ENV PORT="8763"
EXPOSE 8763
COPY --from=java /home/gradle/src/build/libs/phantauth-all.jar /app/
WORKDIR /app
CMD java -jar phantauth-all.jar
