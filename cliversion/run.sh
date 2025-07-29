#!/bin/bash

set -e

kotlinc src/main/kotlin/*.kt -include-runtime -d cliversion.jar

java -jar cliversion.jar
