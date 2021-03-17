<h1 align="center">Welcome to TourGuide 👋</h1>
<p>
  <img alt="Version" src="https://img.shields.io/badge/version-1.0.0-blue.svg?cacheSeconds=2592000" />
</p>

> Application that is used for user to plan their trip. They can use the services of different suppliers to get some prices. The application will also use their localization to provide the attraction nearby. And finally the application help you get reward point when visiting attraction to have price on future trips.

## Technical Conception
![Technical Conception](https://github.com/estated90/JavaPathENProject8/blob/master/Conception%20technique.jpg)

## Usage

```sh
cd TourGuide\
```

```sh
gradlew bootRun
```

## Run tests

```sh
cd TourGuide\
```

```sh
gradlew test
```

## 

1. Install 

   [Java]: https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html	"java installation guide"

2. Install 

   [Gradle]: https://gradle.org/install/	"Gradle installation guide"

3. Install Docker app : 

   [here]: https://docs.docker.com/docker-for-windows/install/	"Docker install for windows"

4. Install the Docker environment. From the folder of the application :

```sh
cd MicroService\
```

```sh
docker-compose up -d
```

One Docker with three container will be created.

- gpsUtils: localhost:8081
- RewardsService: localhost:8082
- TripPricer : localhost:8083

## API

#### Main Application:

TourGuide:

- http://localhost:8080/swagger-ui.html#/

#### Micro-services:

gpsUtils:

- http://localhost:8081/swagger-ui.html#/

RewardsService:

- http://localhost:8082/swagger-ui.html#/

TripPricer:

- http://localhost:8083/swagger-ui.html#/

## Configurations

#### Main Application:

TourGuide:

- Java 8
- Gradle 6.8.2
- Spring Boot 2.4.2

#### Micro-services:

gpsUtils:

- Java 8
- Gradle 6.8.2
- Spring Boot 2.4.2

RewardsService:

- Java 8
- Gradle 6.8.2
- Spring Boot 2.4.2

TripPricer:

- Java 8
- Gradle 6.8.2
- Spring Boot 2.4.2

## Author

👤 **Nicolas**

* Github: [@Estated90](https://github.com/Estated90)

## Show your support

Give a ⭐️ if this project helped you!

***
_This README was generated with ❤️ by [readme-md-generator](https://github.com/kefranabg/readme-md-generator)_
