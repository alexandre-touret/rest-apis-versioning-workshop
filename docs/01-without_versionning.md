# How to upgrade your API without versionning?

At this point we have our first customer : **John Doe** who uses our API with the current specification.  

## Prerequisites

You have to start three new shells and run [rest-book](../rest-book), [rest-number](../rest-number) and [the gateway](../gateway) modules.
As earlier, you must be at the root of the project (i.e., ``rest-apis-versionning-workshop``).

<details>
<summary>Click to expand</summary>

In the first shell, run:

```jshelllanguage
./gradlew bootRun -p rest-book
```

In the second one:

```jshelllanguage
./gradlew bootRun-p rest-number
```

And in the last one:

```jshelllanguage
./gradlew bootRun-p gateway
```

_You can disable unit and integration tests by adding the option ``-x test`` at the end of the command ;-)._

</details>

## The current status

### Getting the OpenAPI Documentation

You can now reach the current API documentation by running these commands:

For the books API:

```jshelllanguage
http :8082/v3/api-docs
```
For the numbers API:

```jshelllanguage
http :8082/v3/api-docs
```

You can also check the documentation by browsing these endpoints:

* http://localhost:8082/swagger-ui/index.html
* http://localhost:8081/swagger-ui/index.html

You can also use the scripts located in the [bin](../bin) folder.

* Get a Random Book

You can get a random book by running this command:

```jshelllanguage
. ./bin/randomBook.sh
```
* Create a book

```jshelllanguage
. ./bin/createBook.sh
```

Now you can stop this service.

## Adding new data

In this chapter, we will update the [Book schema in the OpenAPI spec file](../rest-book/src/main/resources/openapi.yml) adding the attribute ``excerpt``.

This attribute is just the beginning of the [description attribute](../rest-book/src/main/resources/openapi.yml).
We will extract the first 100 characters.

1. Update the [OpenAPI spec file]((../rest-book/src/main/resources/openapi.yml)), add the ``excerpt`` attribute 

```yaml
    Book:
      required:
        - title
      type: object
      properties:
        excerpt:
          readOnly: true
          type: string
```
2. Build the application again

```jshelllanguage
./gradlew build -p rest-book
```

3. Normally you can see now this new attribute in the [BookDto class](../rest-book/build/generated/src/main/java/info/touret/apiversionning/book/generated/dto/BookDto.java). 
4. In the [Book entity class](../rest-book/src/main/java/info/touret/bookstore/spring/book/entity/Book.java), add a transient attribute as below

```java

private @Transient excerpt;

// getter

@PostConstruct
private initFields(){
    // Extract the first 100 characters of the description
    this.excerpt=getDescription().substring(0,100);
}
```
You can now rebuild the application

Before creating unit and integration tests, we can run them to see if this modification is blocking.

<details>
<summary>Click to expand</summary>

Run the tests with gradle 

```jshelllanguage
./gradlew build -p rest-book
```
</details>

5. You can add a test in the [BookServiceTest](../rest-book/src/test/java/info/touret/bookstore/spring/book/service/BookServiceTest.java)
<details>
<summary>Click to expand</summary>

For instance:


```java
   @Test
    void should_find_a_random_book_with_excerpt() {
        var longList = createBookList().stream().map(Book::getId).collect(Collectors.toList());
        when(bookRepository.findAllIds()).thenReturn(longList);
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.of(book));
        assertNotNull(bookService.findRandomBook());
        var book = bookService.findRandomBook();
        assertEquals(book.getDescription().substring(0,100),book.getExcerpt());
    }
```
</details>

You can also add a similar test in the [BookControllerIT](../rest-book/src/test/java/info/touret/bookstore/spring/book/controller/BookControllerIT.java) integration test. 

Now you can re-build your application and validate it by running tests.

```jshelllanguage
./gradlew build -p rest-book
```

6. Now, let's get a random book with an excerpt

You can restart your rest-book service

<details>
<summary>Click to expand</summary>

```jshelllanguage
./gradlew bootRun -p rest-book
```
</details>

## Adding new operations

You can also add a new operation getExcerpt

You just added a new data and functionality without versionning

## What about backward compatibiliy?

Let's create a additional test with the [goold old BookDto definition](../rest-book/build/generated/src/main/java/info/touret/apiversionning/book/generated/dto/BookDto.java).

Copy paste this class in your [test source directory](../rest-book/src/test/java/) and remove the new attribute and operation created earlier. 
You can rename it ``OldBookDto`` for example.

In the [BookControllerIT](../rest-book/src/test/java/info/touret/bookstore/spring/book/controller/BookControllerIT.java), add a new test method:

```java
@Test
    void should_get_a_random_book_with_old_contract() {
        var bookDto = testRestTemplate.getForEntity(booksUrl + "/random", OldBookDto.class).getBody();
        assertNotNull(bookDto.getId());
    }
```

See what happens.