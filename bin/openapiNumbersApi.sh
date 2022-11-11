#! /bin/bash
curl -s -w "\n" localhost:8081/v3/api-docs | jq