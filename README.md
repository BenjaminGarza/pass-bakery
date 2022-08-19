# pass-bakery

## Technologies used
* Play framework
* Circe
* Jdbc
* Postgres
* Doobie

## Setup
1. Install or verify versions
   1. Java 11.0.11
   2. Postgres 14.4
   3. Docker
      1. May need to create an account or login if "ERROR: toomanyrequests" is encountered
2. Launch Docker daemon (Desktop client)
3. Open the terminal and pull the docker image: ```docker pull postgres```
4. Start Postgres instance with docker
   1. ```docker run --name pass-bakery -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=pass -e POSTGRES_DB=bakerydb -p 5500:5432 -d postgres```
   2. Connect with ```psql -h localhost -p 5500 -d bakerydb -U postgres --pass```
   3. On another terminal, navigate to project root and use ```sbt run``` to start pass-bakery
   4. Visit an endpoint and apply evolutions when prompted
