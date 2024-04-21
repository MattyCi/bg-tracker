# SeasonGG

This repo is called bg-tracker (board game tracker) and it contains all of the backend and fronted code for an application I've created called SeasonGG.
This project is a webapp that allows users to track board game "seasons" competitively against friends. At the end of each season, one player is titled a champion.

The project is still under construction and the code within is being migrated to new repos.

## Running Locally

Using `pom.xml` as a reference, set the necessary environment variables needed to connect to a local running instance
of mysql.

Start a mysql server locally (TODO: schema generation?)

Run `mvn package`

Run `java -jar target/dependency/webapp-runner.jar .\target\bg-tracker.war`
