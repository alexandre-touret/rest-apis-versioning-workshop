#!/bin/bash
curl -s -w "\n" -H "Content-Type: application/json" localhost:8888/maintenance | jq
