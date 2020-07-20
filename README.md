# pokemonApi  &middot;  [![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/chrismarquez/pokemonApi/blob/master/LICENSE) [![<ORG_NAME>](https://circleci.com/gh/chrismarquez/pokemonApi.svg?style=svg)](https://circleci.com/gh/chrismarquez/pokemonApi)
This is a REST API for battle ready Pokemón!

Through this simple service, you can have valuable information in-handy, such as:

- Comparing damage and resistance advantages between two pokemon
- Finding out shared moves between two pokemon

And more incoming!

## Use & Installation

This service is publicly available at [pokemon-api.ddns.net](http://pokemon-api.ddns.net)

No access token or additional authentication is required to use this API (Yay!!) .

If you click in the link above, you will be directed to the OpenApi docs of the service.
Also, inside that page, you can try out the API conveniently.

All supported API methods and their related parameters can be found in the OpenApi docs.

#### Prerequisites for local installation

1. Java SDK version 11 or higher
2. Docker (To run as a container)

To run this service locally, after cloning this repository:
- If you have a macOS or Linux system, run `./gradlew run` in your terminal.
- If you have a Windows system, run `gradlew.bat run` in the command line.

To build a standalone Jar file:
- If you have a macOS or Linux system, run `./gradlew shadowJar` in your terminal.
- If you have a Windows system, run `gradlew.bat shadowJar` in the command line.

To run all tests:
- If you have a macOS or Linux system, run `./gradlew test` in your terminal.
- If you have a Windows system, run `gradlew.bat test` in the command line.

If you are using the IntelliJ IDEA IDE (recommended), to start working open the project
with the editor as a Gradle project. The IDE will pickup the project configuration and 
set up accordingly.

## Contributing

If you wish to contribute, please follow the following guidelines
- After cloning the repository, create a new branch to build a new feature / fix
- When commiting, use [Semantic Commit Messages](https://www.conventionalcommits.org/en/v1.0.0/) to improve readability and organization.
- Abide by the [Kotlin Style Guide](https://kotlinlang.org/docs/reference/coding-conventions.html). Let's keep the repo nice and tidy.
- Each commit you make triggers a build at the CircleCI pipeline (check badge at top), use it to check your progress and make sure no tests are broken :)
- When you feel ready, create a pull request towards the master branch
- If approved, your changes will be included, and the CircleCI pipeline will deploy the service with the newest changes, Yay!!
- Be sure to check out PR comments for feedback.
- Kudos to you, great job! Go rest and have a well deserved beer!

## Thanks

Kudos to [PokeAPI](https://pokeapi.co/) for providing open data that made this project possible.

## Maintainers
[@chrismarquez](http://github.com/chrismarquez)
