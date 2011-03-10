#!/bin/sh

# This script is for continuous integration. Generates a test project from
# the archetype and then runs `mvn test` on it to make sure everything works.

mkdir temp
cd temp
mvn2 archetype:generate -B -U \
    -DarchetypeGroupId=com.55minutes \
    -DarchetypeArtifactId=fiftyfive-wicket-archetype \
    -DarchetypeRepository=http://opensource.55minutes.com/maven \
    -DarchetypeVersion=3.0-SNAPSHOT \
    -DgroupId=com.55minutes \
    -DartifactId=test-project \
    -Dversion=999 \
    -Dpackage=fiftyfive.test \
    -Dproject_name=Test && \
cd test-project && \
mvn2 -B test
