#! /bin/bash

curl -s \
  -w "\n" \
  'localhost:8080/books/random' | jq
