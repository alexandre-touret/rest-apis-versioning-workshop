# Configuration management

## TL;DR: What are you going to learn in this chapter?

> [!IMPORTANT]
> This chapter covers the following topics:
> 1. Pinpoint Configuration management issues due to API Versioning
> 2. Exposing the two versions on the API Gateway

## Preamble
You can configure your services either during deployment using CI tooling, such as [Gitlab Environments](https://docs.gitlab.com/ee/ci/environments/), or any other [Infra As Code](https://en.wikipedia.org/wiki/Infrastructure_as_code) tool ([Istio](https://istio.io/), [Ansible](https://www.ansible.com/),...) or using a configuration server.
For this workshop, all the configuration items will be provided by [Spring Cloud Config](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#_quick_start).

We will illustrate in this chapter the impacts of versioning in the configuration management.

Here are the issues to fix in this chapter:
* Specify a different port number for the new rest-book version
* Specify a new version number on all the layers
* Apply different parameters for the number of results of the ``/books`` API, timeout,...

> [!WARNING]
> Now you **MUST** stop all your Spring apps.
> 

## Configuration server version management

For this workshop, we will only carry out a [simple version management based on Spring profiles](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#_quick_start).

Copy / paste the [rest-book.yml](../config-server/src/main/resources/config/rest-book.yml) to [rest-book-v1.yml](../config-server/src/main/resources/config/rest-book-v1.yml) and [rest-book-v2.yml](../config-server/src/main/resources/config/rest-book-v2.yml).

In the latter, **UPDATE** the properties ``server.port`` and ``server.servet.context-path``:

```yaml
server:
  port: 8083
  servlet:
    context-path: /v2
```

You can also remove the ``book.find.limit`` property in the first version and modify the ``booknumbers.api.timeout_sec`` property in the second one.

Now, start your config server, **RUN**:

```jshelllanguage
./gradlew clean bootRun -p config-server
```

Test it using these HTTP requests:

```jshelllanguage
http :8888/rest-book/v1 --print b | jq ' .propertySources[0].source' | jq '."server.servlet.context-path"'
```
You must get this output:

```jshelllanguage
    "/v1"
```
and

```jshelllanguage
http :8888/rest-book/v2 --print b | jq ' .propertySources[0].source."server.servlet.context-path"'
```
You must get this output:

```jshelllanguage
    "/v2"
```

## Rest-book configuration management

First, **UPDATE** the ``application.properties`` files to specify the current profile:

In the [V1](../rest-book/src/main/resources/application.properties):

```properties
spring.profiles.active=v1
```

And in the [V2](../rest-book-2/src/main/resources/application.properties):

```properties
spring.profiles.active=v2
```

### OpenAPI

**UPDATE** [the rest-book v1 OpenAPI description file](../rest-book/src/main/resources/openapi.yml) to specify the new version:

```yaml
openapi: 3.0.0
info:
  title: OpenAPI definition
  version: "v1"
servers:
  - url: http://localhost:8082/v1
```

**UPDATE** [the rest-book v2 OpenAPI description file](../rest-book-2/src/main/resources/openapi.yml) to specify the new version:

```yaml
openapi: 3.0.0
info:
  title: OpenAPI definition
  version: "v2"
servers:
  - url: http://localhost:8083/v2
```

### Test it

First, stop the config server, and build the whole application:

```jshelllanguage
./gradlew clean build
```

The build **MUST** be successful.

Start your backends (we assume your Docker infrastructure is still up).

In the first shell, **RUN**:

```jshelllanguage
./gradlew bootRun -p config-server
```
In the second shell, **RUN**:

```jshelllanguage
./gradlew bootRun -p rest-book-2
```

In the third shell, **RUN**:

```jshelllanguage
./gradlew bootRun -p rest-book
```
In the fourth shell, **RUN**:

```jshelllanguage
./gradlew bootRun -p rest-number
```

Now, reach your APIs (without using the gateway):

**RUN** for the V1:
```jshelllanguage
http :8082/v1/books 
```

and the V2, **RUN**:
```jshelllanguage
http :8083/v2/books
```

## Rest-number

This service doesn't really need to be versioned now.
To put in place the whole infrastructure and the same behaviour of rest-book module, we will apply the same configuration:

* Stop the config server.
* **UPDATE** the [rest-number.yml](../config-server/src/main/resources/config/rest-number.yml) configuration file renaming it to [rest-number-v1.yml](../config-server/src/main/resources/config/rest-number-v1.yml)
* Restart the config server

```jshelllanguage
./gradlew bootRun -p config-server
```

* **UPDATE** the [rest-number application.properties file](../rest-number/src/main/resources/application.properties) defining the current profile:

```properties
spring.profiles.active=v1
```

* **RUN** then the rest-number module:

```jshelllanguage
./gradlew bootRun -p rest-number
```

## Gateway configuration

Now, we will expose both versions in the gateway.

**UPDATE** the [gateway configuration file](../gateway/src/main/resources/application.yml), adding the following content:

```yaml
        # HTTP HEADER VERSIONING
        - id: rewrite_v2
          uri: http://127.0.0.1:8083
          predicates:
            - Path=/books/{segment}
            - Header=X-API-VERSION, v2
          filters:
            - RewritePath=/books/(?<segment>.*),/v2/books/$\{segment}
        - id: rewrite_v2
          uri: http://127.0.0.1:8083
          predicates:
            - Path=/books
            - Header=X-API-VERSION, v2
          filters:
            - RewritePath=/books,/v2/books
        - id: rewrite_v2
          uri: http://127.0.0.1:8081
          predicates:
            - Path=/isbns
            - Header=X-API-VERSION, v2
          filters:
            - RewritePath=/isbns,/v1/isbns
        # HTTP ACCEPT MEDIA TYPE HEADER VERSIONING
        - id: rewrite_accept_v2
          uri: http://127.0.0.1:8083
          predicates:
            - Path=/books
            - Header=accept, application/vnd.api\.v2\+json
          filters:
            - RewritePath=/books,/v2/books
        - id: rewrite_accept_v2
          uri: http://127.0.0.1:8083
          predicates:
            - Path=/books/{segment}
            - Header=accept, application/vnd.api\.v2\+json
          filters:
            - RewritePath=/books/(?<segment>.*),/v2/books/$\{segment}
        - id: rewrite_accept_v2
          uri: http://127.0.0.1:8081
          predicates:
            - Path=/isbns
            - Header=accept, application/vnd.api\.v2\+json
          filters:
            - RewritePath=/isbns,/v1/isbns
        # URI PATH VERSIONING
        - id: path_route
          uri: http://127.0.0.1:8083
          predicates:
            - Path=/v2/books
        - id: path_route
          uri: http://127.0.0.1:8083
          predicates:
            - Path=/v2/books/{segment}
        - id: path_route
          uri: http://127.0.0.1:8081
          predicates:
            - Path=/v2/isbns
          filters:
            - RewritePath=/v2/isbns,/v1/isbns


```

To propose a cohesive and coherent API to our customer, we chose to publish all our API endpoints with a v1 and v2 prefix.
Although [rest-number](../rest-number) only provides __ONE__ version (i.e., the ``v1``), we will expose both on the gateway.

**UPDATE** the configuration file adding this configuration:

```yaml
        - id: path_route
          uri: http://127.0.0.1:8081
          predicates:
            - Path=/v2/isbns
          filters:
            - RewritePath=/v2/isbns,/v1/isbns
```

### Test it
Restart your gateway and test it:

```jshelllanguage
./gradlew clean bootRun -p gateway
```

You can now use [the scripts files](../bin).
For every script (e.g., [``countBooks.sh``](../bin/countBooks.sh)), you have one which reach the V2 endpoints (e.g., [``countBooks-v2.sh``](../bin/countBooks-v2.sh)) 

Here is an example for the ``countBooks.sh`` script file copied to ``countBooks-v2.sh``.

```jshelllanguage
http :8080/v2/books/count
```
This action is voluntary simple.
Feel free to add an argument to the existing script files if you want ;-).

> [!NOTE]
> In this chapter, we have seen one part of the impacts of API versioning in configuration management. The most important part is done before, both in the GIT configuration and the release management.
>
> [Go then to chapter 5](05-conflicts.md)
