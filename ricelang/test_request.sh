#!/usr/bin/env bash

# make sure that your sprintboot compiler server is running
# ./gradlew bootRun

# curl -H 'Content-Type: application/json' \
#     -d '{ "sourceCode":"int main() { putStringLn(\"hello from curl request\");\n putIntLn(1 + 2 + 3 + 4);\n putBoolLn(true);\n return 0; }"}' \
#     -X POST \
#     127.0.0.1:8080/run

curl -H 'Content-Type: application/json' \
    -d '{ "sourceCode":"int main() {int i = 0; while (i < 5) {putIntLn(i);i = i + 1;}byebye 0;}"}' \
    -X POST \
    https://ricelang-playground.onrender.com/ast