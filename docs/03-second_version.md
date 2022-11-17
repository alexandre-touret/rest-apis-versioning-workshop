# And now something completely different : a second version

## A new functionality for a new customer

We have now a new customer. The good point is our API tends to be famous, the bad point is we need to change our API contract without impacting our existing customers.
The very bad point, is our existing customers cannot update their API clients before one year (at least).
We then decided to create a new version!

Now our customer wants to enable having several authors for a same book.
Currently, one book could only have one author.

In this case, it is strongly recommended to deal with GIT long time versions. 
For instance, using [Gitflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow).

To simplify the development loop of this workshop, we will duplicate the [rest-book](../rest-book) module.

### Duplicating the rest-book module

* Copy/paste the [rest-book module](../rest-book)
* Rename the new folder as ``rest-book-2``
* Update the [build.gradle] with the configuration below:

```groovy
project(':rest-book-2') {
    dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
        runtimeOnly 'org.postgresql:postgresql'
        testImplementation 'com.h2database:h2'
        implementation 'org.springframework.boot:spring-boot-starter-web'
        implementation 'org.springframework.boot:spring-boot-starter-validation'
        implementation 'org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j'
        implementation 'org.springframework.cloud:spring-cloud-starter-config'
        implementation 'io.github.resilience4j:resilience4j-spring-boot2'
        implementation 'org.springframework.boot:spring-boot-starter-aop'
        implementation 'org.springdoc:springdoc-openapi-ui:1.6.9'
        implementation 'com.fasterxml.jackson.core:jackson-annotations'
    }
}
```

In the [settings.gradle](../settings.gradle) file you have to define this new module:

```properties
include 'rest-book-2'
```

Validate your configuration by building this project:

```jshelllanguage
./gradlew build
```

You can also only build the new module by running this command :

```jshelllanguage
./gradlew build -p rest-book-2
```

## Adding a new functionality



Routing in the gateway




