#! /bin/bash

curl -s \
  -w "\n" \
  'localhost:8888/books/random' | jq
