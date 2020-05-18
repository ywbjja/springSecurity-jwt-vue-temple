FROM openjdk:8u181-jdk-alpine
VOLUME /tmp
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY ./target/security-0.0.1-SNAPSHOT.jar security-java.jar
EXPOSE 8086
ENTRYPOINT ["java","-jar","-Xms128m","-Xmx256m","/security-java.jar"]
