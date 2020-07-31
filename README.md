# JaCaMo-REST: Integration Demo (Apache Camel + MQTT)

## Running with Docker
#### Prerequisites:
- [Docker](https://docs.docker.com/engine/install/)
- [Docker Compose *](https://docs.docker.com/compose/install/)
> \* On desktop systems like Docker Desktop for Mac and Windows, Docker Compose is included as part of those desktop installs.



1. Make sure you are in the root directory of this project

2. Open a command-line shell and run Camel and JaCaMo applications via Docker Compose:

  ```
  docker-compose up
  ```

## To try this demo

1. __Create the dummy entities -__ Open a browser and use the following URL:

  ```
  http://localhost:8090/demo/dummies
  ```

2. __Ask ana to send a message to the MQTT broker via dummy agent -__ Use the following URL:

  ```
  http://localhost:8090/demo/ana
  ```

3. __Ask bob to send a message to the MQTT broker via dummy artifact -__ Use the following URL:

  ```
  http://localhost:8090/demo/bob
  ```
  
  __(Under Construction)__