FROM openjdk:8-alpine

COPY target/uberjar/clojure-clothes.jar /clojure-clothes/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/clojure-clothes/app.jar"]
