# properties-parser
How to run:
- build using gradle: ./gradlew build (without ./ on windows: gradlew build)
- run "java -jar demo-0.0.1-SNAPSHOT.jar leaf_path"

# example
java -jar demo-0.0.1-SNAPSHOT.jar /configs/dev/east/node1

leaf path will traversed from deepest property file (/configs/dev/east/node1/config.properties)
to last config directory (/configs/config.properties), excluding the current directory
