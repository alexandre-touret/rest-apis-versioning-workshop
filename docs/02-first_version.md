# Your first version

We will define in this chapter our first version in the URI and in a header mixing between the gateway & the apps.

> **Warning**
>
> Before starting, this chapter, please shut down all the spring apps already started:

* [config server](../config-server)
* [gateway](../gateway)
* [authorization server](../authorization-server)
* [rest-book](../rest-book)
* [rest-number](../rest-number)

## URI based version creation

In the curent rest-book version,we will define the version in the BookController's URI.

The pattern should be like ``/api/%VERSION%/books``.
For instance, we could have ``/api/v1/books``.

Here is how we could implement it both in the backends and in the gateway.

### Configuration

#### Rest-Book

Update the [rest-book's openAPI descriptor file](../rest-book/src/main/resources/openapi.yml) adding the version in the URL:

```yaml
openapi: 3.0.0
info:
  title: OpenAPI definition
  version: "v1"
servers:
  - url: http://localhost:8082/v1
```

You can now override the default context path in the [rest-book configuration file](../config-server/src/main/resources/config/rest-book.yml)

```yaml
server:
  servlet:
    context-path: /v1
```

Now, try to build the project:

```bash 
./gradlew build -p rest-book
``` 

Update then your unit tests to reflect the version handling:

* Add the same property in the [application.yml test configuration file](../rest-book/src/test/resources/application.yml)

* In the ``setUp`` method of [BookControllerIT](../rest-book/src/test/java/info/touret/bookstore/spring/book/controller/BookControllerIT.java) and [OldBookControllerIT](../rest-book/src/test/java/info/touret/bookstore/spring/book/controller/OldBookControllerIT.java) integration tests, modify the basepath

```java
@BeforeEach
void setUp() {
booksUrl ="http://127.0.0.1:" + port + "/v1/books";
mockServer = MockRestServiceServer.bindTo(restTemplate).build();
mockServer.reset();
}

```

* To get your unit tests successfull, you will also have to modify this test ``should_register_a_book_successfully()`` by modifying this assertion:

from:
```java
assertTrue(uri.getPath().matches("/books/[1-9]+$"));
```
to:

```java
assertTrue(uri.getPath().matches("/v1/books/[1-9]+$"));
```

* In the [MaintenanceControllerIT](../rest-book/src/test/java/info/touret/bookstore/spring/maintenance/controller/MaintenanceControllerIT.java) test class, you have to modify the ``setUp()`` method in the same way than earlier:

```java
@BeforeEach
void setUp() throws Exception {
    maintenanceUrl = "http://127.0.0.1:" + port + "/v1/maintenance";
    booksUrl = "http://127.0.0.1:" + port + "/v1/books";
```

Build the application and run it:

```bash 
./gradlew bootRun -p rest-book
``` 

##### Looking forward to rest-number api versioning updates

This module reaches [rest-number](../rest-number) through API calls.
It will be versioned later (see below).
We need to anticipate these changes in this module:

In the [rest-book configuration file](../config-server/src/main/resources/config/rest-book.yml) , modify the following property adding the version:

```yaml
booknumbers:
  api:
    url: http://127.0.0.1:8081/v1/isbns
```

Update the same property in the rest-book [application.yml test configuration file](../rest-book/src/test/resources/application.yml)
and finally, update the mock configuration in the test classes:


#### Rest-Number

Update the [rest-number's openAPI descriptor file](../rest-number/src/main/resources/openapi.yml) adding the version in the URL:

```yaml
openapi: 3.0.1
info:
  title: OpenAPI definition
  version: v1
servers:
  - url: http://localhost:8081/v1
```

You can now override the default context path in the [rest-number configuration file](../config-server/src/main/resources/config/rest-number.yml):

```yaml
server:
  servlet:
    context-path: /v1
```

Now, try to build the project:

```bash 
./gradlew build -p rest-number
``` 

Update then your unit tests to reflect the version handling:

Add the same property in the [application.yml test configuration file](../rest-number/src/test/resources/application.yml)

To get your unit tests successful, you will also have to modify the [BookNumbersControllerIT](../rest-number/src/test/java/info/touret/bookstore/spring/number/controller/BookNumbersControllerIT.java) and [BookNumberControllerTimeoutIT](../rest-number/src/test/java/info/touret/bookstore/spring/number/controller/BookNumbersControllerTimeoutIT.java) test classes by modifying this line:

from:

```java
var response = restTemplate.getForEntity("http://127.0.0.1:" + port + "/isbns", BookNumbersDto.class);
```
to:
```java
var response = restTemplate.getForEntity("http://127.0.0.1:" + port + "/v1/isbns", BookNumbersDto.class);
```

Build the application and run it:

```bash 
./gradlew bootRun -p rest-number
``` 

### In the gateway

Update the corresponding routes defined in the [gateway application.yml configuration file](../gateway/src/main/resources/application.yml).

<details>
<summary>Click to expand</summary>

```yaml
spring:
 application:
  name: gateway
 zipkin:
  base-url: http://localhost:9411
  sender:
   type: web
 cloud:
  gateway:
   routes:
    - id: path_route
      uri: http://127.0.0.1:8082
      predicates:
       - Path=/v1/books
    - id: path_route
      uri: http://127.0.0.1:8082
      predicates:
       - Path=/v1/books/{segment}
    - id: path_route
      uri: http://127.0.0.1:8081
      predicates:
       - Path=/v1/isbns
```
</details>

### Tests

#### Startup

Normally, you Docker infrastructure should be up. If not, start it:

<details>
<summary>Click to expand</summary>

```jshelllanguage
cd infrastructure 
docker compose up
```
</details>


Start then the different applications:

<details>
<summary>Click to expand</summary>
In the first shell:

```jshelllanguage
./gradlew bootRun -p config-server
```
In the second shell:

```jshelllanguage
./gradlew bootRun -p authorization-server
```

In the third shell:

```jshelllanguage
./gradlew bootRun -p rest-book
```
In the fourth shell:

```jshelllanguage
./gradlew bootRun -p rest-number
```

Last but not least, in the last one:

```jshelllanguage
 ./gradlew bootRun -p gateway
```

</details>


You can now reach the API.

For instance, you can reach the gateway:

```jshelllanguage
http :8080/v1/books/count
```

You can also access directly to the rest-book backend:

```jshelllanguage
http :8082/v1/books/count
```

Now you can update in the same way [your scripts](../bin) adding the version prefix.

### Create a HTTP Header based version

In the [rest-numbers project](../rest-numbers), we will apply a HTTP Header based version in the [BookNumbersController](./../rest-number/src/main/java/info/touret/bookstore/spring/number/controller/BookNumbersController.java) class.

We will use the ``X-API-VERSION`` http header to specify it.

### Creation an "accept media" header

We could also use the accept media type header :



TODO: trouver un exemple
```
Accept: application/vnd.myapi.v2+json
```
