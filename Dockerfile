FROM openjdk:19
MAINTAINER Peter Willems
COPY target/*.jar fuseki.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/fuseki.jar"]
CMD ["hostvol/NL47INGB0002337844_01-01-2023_28-02-2023.csv"]

# docker build -t finance-app .
# docker run --rm -it -p 8080:8080 -v ${PWD}:/hostvol finance-app