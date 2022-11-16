#! /bin/bash


access_token=`http --form post :8009/oauth2/token grant_type="client_credentials" client_id="customer1" client_secret="secret1" -p b | jq -r '.access_token'`

curl -w "\n" -X POST -d '{"title":"Practising Quarkus", "author":"Antonio Goncalves", "yearOfPublication":"2020"}' -H "Content-Type: application/json" -H "Authorization: Bearer ${access_token}" localhost:8080/books
