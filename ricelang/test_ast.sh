#!/usr/bin/env bash

# make sure that your sprintboot compiler server is running
# ./gradlew bootRun

curl -H 'Content-Type: application/json' \
    -d '{ "sourceCode":"int main() { putStringLn(\"hello world\");\nreturn 0; }" }' \
    -X POST \
    127.0.0.1:8080/ast