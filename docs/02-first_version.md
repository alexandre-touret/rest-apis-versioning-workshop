# Applying modifications without versioning 

In the URI, in a header, a mix between the gateway & the apps

## Create a URI based version

In the curent rest-book version, apply a version in the URI of BookController.

The pattern should be like ``/api/%VERSION%/books``. 
For instance, we could have ``/api/v1/books``.

### Configuration


Update the openAPI descriptor file.

TODO
Add a version....

Try to build the project first

```bash 
./gradlew build -p rest-book
``` 

See errors

Change unit tests

Build the application and run it

```bash 
./gradlew bootRun -p rest-book
``` 

### In the gateway

Update the corresponding route

resta

### Create a HTTP Header based version

In the rest-numbers project, we will apply a HTTP Header based version in the [BookNumbersController](./../rest-number/src/main/java/info/touret/bookstore/spring/number/controller/BookNumbersController.java) class.

We will use the ``X-API-VERSION`` http header to specify it.
We could also use  the accept media type header :

```
Accept: application/vnd.myapi.v2+json
```





