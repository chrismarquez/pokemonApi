
FROM adoptopenjdk/openjdk11:alpine

RUN mkdir /app
WORKDIR /app

COPY /build/libs/PokemonAPI-1.0-all.jar .


ENV APPLICATION_USER ktor
ENV RUN_ENVIRONMENT Prod
RUN adduser -D -g '' $APPLICATION_USER
RUN chown -R $APPLICATION_USER /app

EXPOSE 8080:8080

USER $APPLICATION_USER

CMD ["java", "-jar", "PokemonAPI-1.0-all.jar"]