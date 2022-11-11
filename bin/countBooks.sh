#! /bin/bash

curl -s \
  -w "\n" \
  'localhost:8080/books/count' \
  -H 'accept: application/json' | jq
