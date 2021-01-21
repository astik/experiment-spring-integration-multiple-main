# Experiment: Spring-integration - multiple main

This project aims to experiment with spring-integration in a context of multiple application bundled in the same fatjar.

To do so, we will need :

- a way to have common beans that will be loaded for each application
- a way to have specific beans that will be loaded only for specific applications

For the sake of simplicity, custom properties are hard coded in Main classes.

## How to run

Package the app:

```sh
mvn package
```

Start application example1:

```sh
cp data/source-example/bla.csv data/source1
LOADER_MAIN=fr.smile.poc.example1.Main \
	java -cp target/experiment-spring-integration-multiple-main-0.0.1-SNAPSHOT.jar org.springframework.boot.loader.PropertiesLauncher
```

or application example2:

```sh
cp data/source-example/bla.csv data/source2
LOADER_MAIN=fr.smile.poc.example2.Main \
	java -cp target/experiment-spring-integration-multiple-main-0.0.1-SNAPSHOT.jar org.springframework.boot.loader.PropertiesLauncher
```

or application example3:

```sh
cp data/source-example/bla.csv data/source3
LOADER_MAIN=fr.smile.poc.example3.Main \
	java -cp target/experiment-spring-integration-multiple-main-0.0.1-SNAPSHOT.jar org.springframework.boot.loader.PropertiesLauncher
```

Witness the power of the flow in the log (watch for the `TRACE` and `sysout`).
And the results on the FS:

```sh
ls -1 data/target/
Example_1-bla.csv
Example_2-bla.csv
Example_3-bla.csv
```
