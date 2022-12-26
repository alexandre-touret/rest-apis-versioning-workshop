# How to upgrade your API without versioning?

At this point we have our first customer : **John Doe** who uses our API with the current specification.

## Prerequisites

You have to start three new shells and run [rest-book](../rest-book), [rest-number](../rest-number), [authorization-server](../authorization-server)
and [the gateway](../gateway) modules.
As mentioned earlier, you must be at the root of the project (i.e., ``rest-apis-versioning-workshop``).

<details>
<summary>Click to expand</summary>

In the first shell, run:

```jshelllanguage
./gradlew bootRun -p rest-book
```

In the second one:

```jshelllanguage
./gradlew bootRun -p rest-number
```


And in the last one:

```jshelllanguage
./gradlew bootRun -p gateway
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
http :8081/v3/api-docs
```

You can also check the documentation by browsing these endpoints:

* http://localhost:8082/swagger-ui/index.html
* http://localhost:8081/swagger-ui/index.html

You can also use the scripts located in the [bin](../bin) folder.

Here are some examples of the functionalities provided:

* Get a Random Book

You can get a random book by running this command:

```jshelllanguage
. ./bin/randomBook.sh
```
* Create a book

```jshelllanguage
. ./bin/createBook.sh
```

Now you can stop this service now by typin CTRL+C on the shell you started the rest-book module.

## Adding new data

In this chapter, we will update the [Book schema in the OpenAPI spec file](../rest-book/src/main/resources/openapi.yml) adding the attribute ``excerpt``.

This attribute is (only for the workshop) the beginning of the [description attribute](../rest-book/src/main/resources/openapi.yml).
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

The build and tests should success. In the meantime, you would get this warning message:

```jshelllanguage
...mapper/BookMapper.java:13: warning: Unmapped target property: "excerpt".
   BookDto toBookDto(Book book);

```
It is *"normal"* because the POJO used to persist data has not been modified yet.

3. Normally you can see now this new attribute in
   the [BookDto class](../rest-book/build/generated/src/main/java/info/touret/bookstore/spring/book/generated/dto/BookDto.java)
   .
4. In the [Book entity class](../rest-book/src/main/java/info/touret/bookstore/spring/book/entity/Book.java), add a
   transient attribute as below

```java

@Transient
private transient String excerpt;


// getter

public String getExcerpt(){
        return this.excerpt;
        }


@PostLoad
public void initFields(){
        if(description!=null) {
        this.excerpt = description.substring(0, 100);
        }
        }
```
You can now rebuild the application.

Before creating unit and integration tests, we can run them to see if this modification is blocking.

```jshelllanguage
./gradlew build -p rest-book
```

:question: See what happens: Is it blocking or not?

5. You can add a test in the [BookServiceTest](../rest-book/src/test/java/info/touret/bookstore/spring/book/service/BookServiceTest.java)
<details>
<summary>Click to expand</summary>

For instance:


```java
@Test
 void should_find_a_random_book_with_excerpt() {
         var book = Mockito.mock(Book.class);
        when(book.getId()).thenReturn(100L);
        when(book.getDescription()).thenReturn("""
             Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
             """);
        when(book.getExcerpt()).thenReturn("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut l");
        var longList = createBookList().stream().map(Book::getId).collect(Collectors.toList());
        when(bookRepository.findAllIds()).thenReturn(longList);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        assertNotNull(bookService.findRandomBook());
        var bookFounded = bookService.findRandomBook();
        assertEquals(book.getDescription().substring(0, 100), bookFounded.getExcerpt());
        }
```
</details>

You can also add a similar test in the [BookControllerIT](../rest-book/src/test/java/info/touret/bookstore/spring/book/controller/BookControllerIT.java) integration test.

For instance, you can add this assertion in the [``should_get_a_random_book``](../rest-book/src/test/java/info/touret/bookstore/spring/book/controller/BookControllerIT.java):

```java
@Test
void should_get_a_random_book() {
        var bookDto = testRestTemplate.getForEntity(booksUrl + "/random", BookDto.class).getBody();
        assertNotNull(bookDto.getId());
        assertEquals(bookDto.getDescription().substring(0,100),bookDto.getExcerpt());
        }

```

Now you can re-build your application and validate it by running tests.

```jshelllanguage
./gradlew clean build -p rest-book
```

6. Now, let's get a random book with an excerpt

Restart your rest-book service

```jshelllanguage
./gradlew bootRun -p rest-book
```

Check it manually by running the following command:

```jshelllanguage
http :8082/books/1098 --print b | jq .excerpt 
```

You can also do that through the API Gateway:

```jshelllanguage
http :8080/books/1098 --print b | jq .excerpt 
```
## Adding a new operation

You can then add a new operation ``getBookExcerpt``.

In the [OpenAPI spec file](../rest-book/src/main/resources/openapi.yml), add a new operation:
<details>
<summary>Click to expand</summary>

For instance:

```yaml
  /books/{id}/excerpt:
     get:
        tags:
           - book-controller
        summary: Gets a book's excerpt from its ID
        operationId: getBookExcerpt
        parameters:
           - name: id
             in: path
             required: true
             schema:
                type: integer
                format: int64
        responses:
           '200':
              description: Found book excerpt
              content:
                 application/json:
                    schema:
                       type: string
           '408':
              description: Request Timeout
              content:
                 "*/*":
                    schema:
                       "$ref": "#/components/schemas/APIError"
           '418':
              description: I'm a teapot
              content:
                 "*/*":
                    schema:
                       "$ref": "#/components/schemas/APIError"
           '500':
              description: Internal Server Error
              content:
                 "*/*":
                    schema:
                       "$ref": "#/components/schemas/APIError"

```
</details>

You can now generate the corresponding Java code.

```jshelllanguage
./gradlew  openApiGenerate -p rest-book
```

Now, you can add a new integration test assertion:

In the [BookControllerIT](../rest-book/src/test/java/info/touret/bookstore/spring/book/controller/BookControllerIT.java) class, add the following method:

```java
@Test
void should_find_an_excerpt() throws Exception {
        var responseEntity = testRestTemplate.getForEntity(booksUrl + "/100/excerpt", String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        var excerpt = responseEntity.getBody();
        assertNotNull(excerpt);
        assertEquals("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut l", excerpt);
        }
```

Now, let us create the corresponding method in [BookController](../rest-book/src/main/java/info/touret/bookstore/spring/book/controller/BookController.java):

Add the following method:

```java
   @Override
public ResponseEntity<String> getBookExcerpt(Long id) {
        var optionalBook = bookService.findBookById(id);
        if (optionalBook.isPresent()) {
        return ResponseEntity.ok(optionalBook.get().getExcerpt());
        } else {
        return ResponseEntity.notFound().build();
        }
        }
```

Run tests again:

```jshelllanguage
./gradle build
```

You have now added new data and functionality to your API without any version :exclamation:

## What about backward compatibility?

Let's create a additional test with
the [good old BookDto definition](../rest-book/build/generated/src/main/java/info/touret/bookstore/spring/book/generated/dto/BookDto.java)
.

Copy paste this class in your [test source directory](../rest-book/src/test/java/) and remove the new attribute and
operation created earlier.
You can rename it ``OldBookDto`` for example and put in the package ``info.touret.bookstore.spring.book.dto``.

Copy paste then your [BookControllerIT](../rest-book/src/test/java/info/touret/bookstore/spring/book/controller/BookControllerIT.java)  integration test to [OldBookControllerIT](../rest-book/src/test/java/info/touret/bookstore/spring/book/controller/OldBookControllerIT.java).

In the [OldBookControllerIT](../rest-book/src/test/java/info/touret/bookstore/spring/book/controller/OldBookControllerIT.java)
, replace the ``BookDto`` class usage with the new one.

You also have to modify the test ``should_get_a_random_book()``.
You can remove this line:

```java
assertNotNull(bookDto.getExcerpt());
```

> **Note**
>
> See what happens and **explain it** :exclamation:
>
> [Go then to chapter 2](./02-first_version.md)

