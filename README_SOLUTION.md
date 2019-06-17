# Simplaex Producer/Consumer Task - Solution

My solution is written in scala and requires sbt to build.

Once sbt is installed on the local system the project can either be build using ```sbt assembly``` and then run using ```java -jar "$(pwd)/target/scala-2.12/simplaex-exercise-assembly-0.0.1-SNAPSHOT"``` or directly using ```sbt run```.

For a simple environment that is able to build / run the app the dockerfile .devcontainer/Dockerfile can be used.

# Configuration
My solution uses [pureconfig](https://github.com/pureconfig/pureconfig) to load its configuration. The default values can be found in src/main/resources/application.conf. The output directory can additionally be overriden using the environment variable APP_OUTPUT_DIR.

