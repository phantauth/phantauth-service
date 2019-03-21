FROM gradle:jdk10 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM openjdk:10-jre-slim
ENV PORT="8888" PHANTAUTH_WEBSITE_URL="https://phantauth.me" PHANTAUTH_SERVICE_URL="https://accounts.phantauth.me"
EXPOSE 8888
COPY --from=builder /home/gradle/src/build/libs/phantauth-all.jar /app/
WORKDIR /app
CMD java -jar phantauth-all.jar
