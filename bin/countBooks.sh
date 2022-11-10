#! /bin/bash

curl -s \
  -w "\n" \
  'localhost:8888/books/count' \
  -H 'accept: application/json' | jq
