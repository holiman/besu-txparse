# Besu TxParse

Simple cli tool to parse a transaction from stdin and return the sender, or an error.  

### Build
`./gradlew install`

### Run
`echo "0x02..." |build/install/besu-txparse/bin/besu-txparse`


### Fuzzing 

```
wget -O jacocoagent.jar https://gitlab.com/gitlab-org/security-products/analyzers/fuzzers/javafuzz/-/raw/master/javafuzz-maven-plugin/src/main/resources/jacocoagent-exp.jar
docker run -it -v "`pwd`:/workspace" maven:3.6.3-jdk-11 /bin/bash 
```
Then inside that one
```
cd workspace
mvn install
MAVEN_OPTS="-javaagent:jacocoagent.jar" mvn javafuzz:fuzz -DclassName=org.hyperledger.besu.FuzzTx
```
