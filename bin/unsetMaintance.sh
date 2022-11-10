#!/bin/bash
curl -X 'PUT' \
  'http://localhost:8888/maintenance' \
  -w "\n" \
  -H 'Content-Type: text/plain' \
  -d 'false' -v
