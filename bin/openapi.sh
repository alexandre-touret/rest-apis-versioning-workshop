#! /bin/bash
curl -s -w "\n" localhost:8082/v3/api-docs | jq