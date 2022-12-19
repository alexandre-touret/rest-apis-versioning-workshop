#! /bin/bash
curl -w "\n" -X POST -d '{"title":"Practising Quarkus", "author":"Antonio Goncalves", "yearOfPublication":"2020"}' -H "Content-Type: application/json" localhost:8080/books


http --json post :8080/books  title="Practising Quarkus" author="Antonio Goncalves" yearOfPublication:="2020"
