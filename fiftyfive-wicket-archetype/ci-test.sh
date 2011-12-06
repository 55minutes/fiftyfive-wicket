#!/bin/sh

# This script is for continuous integration. Generates a test project from
# the archetype and then runs `mvn test` on it to make sure everything works.

mvn clean
mkdir temp
cd temp
mvn archetype:generate -B -U \
    -DarchetypeGroupId=com.55minutes \
    -DarchetypeArtifactId=fiftyfive-wicket-archetype \
    -DarchetypeRepository=http://opensource.55minutes.com/maven-snapshots \
    -DarchetypeVersion=3.1-SNAPSHOT \
    -DgroupId=com.55minutes \
    -DartifactId=test-project \
    -Dversion=999 \
    -Dpackage=fiftyfive.test \
    -Dproject_name=Test && \
cd test-project && \
mvn -B test
