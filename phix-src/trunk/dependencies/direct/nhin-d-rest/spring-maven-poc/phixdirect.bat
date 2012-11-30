cd C:\nhin-d-rest\spring-maven-poc\support
mvn install:install-file -DgroupId=org.nhind -DartifactId=nhin-d-agent -Dversion=1.0.0-SNAPSHOT -Dpackaging=jar -Dfile=nhin-d-agent-1.0.0-SNAPSHOT.jar >> output.log
cd C:\nhin-d-rest\spring-maven-poc\
mvn -e jetty:run >> C:\nhin-d-rest\spring-maven-poc\output.log
