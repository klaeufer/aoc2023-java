#!/bin/bash

echo skipping Kotlin version
#date
#MAVEN_OPTS="--enable-preview" mvn exec:java -Dexec.mainClass=Day5k
#date

echo running Java version
date
/usr/bin/time -f "java,%C,%E,%S,%U,%P,%M,%t,%K,%D,%p,%X,%Z,%I,%O,%r,%s,%k,%W,%c,%w,%R,%x" mvn exec:java -Dexec.mainClass=Day5
date
