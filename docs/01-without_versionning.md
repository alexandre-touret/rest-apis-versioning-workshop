# How to upgrade your API without versionning?

At this point we have our first customer : **John Doe** who uses our API with the current specification.  

You can reach the current API by running this command :

* Get a Random Book

* Create a book

## Adding new data

* Update Book DTO in the OpenAPI spec file adding the attribute ``excerpt``
* Build the application
* Check with unit tests if it is OK

## Adding new operations

You can also add a new operation getExcerpt

You just added a new data and functionality without versionning

## Changes 

add deprecated

Update openapi

see consequences