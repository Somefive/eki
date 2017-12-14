#!/usr/bin/env bash
sbt compile dist
cd target/universal
unzip -q -o eki-1.0-SNAPSHOT.zip
cd ../..