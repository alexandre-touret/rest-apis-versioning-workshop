package info.touret.bookstore.spring.book.controller;

import info.touret.bookstore.spring.book.entity.Book;
import info.touret.bookstore.spring.book.service.BookService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Book REST API.
 * the API time to respond is monitor using <code>@Timed</code> annotation
 * @see Timed
 */
@RestController()
@Timed(value = "bookController")
@RequestMapping(value = "/api/books", produces = APPLICATION_JSON_VALUE)
public class BookController {
    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Gets a random book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Book.class))})})
    @GetMapping("/random")
    public ResponseEntity<Book> getRandomBook() {
        return ResponseEntity.ok(bookService.findRandomBook());
    }

    @Operation(summary = "Gets all books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found books",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "204", description = "No books found")})
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAllBooks());
    }

    /**
     * Returns the number of books
     *
     * @return A Map which will be automatically transformed into a JSON Object
     */
    @Operation(summary = "Gets the number of books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Number of books",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Map.class))})})
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> count() {
        return ResponseEntity.ok(Map.of("books.count", bookService.count()));
    }

    @Operation(summary = "Gets a book from its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found book",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Book.class))})})
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@NotNull @PathVariable("id") Long id) {
        return ResponseEntity.of(bookService.findBookById(id));
    }

    @Operation(summary = "Creates a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "The book has not been yet created but will be ASAP"),
            @ApiResponse(responseCode = "408", description = "The number api is unreachable"),
            @ApiResponse(responseCode = "500", description = "An unexpected error has occured"),
            @ApiResponse(responseCode = "201", description = "Book created. The URI is filled in the Location header",
                    headers = {@Header(name = "location", schema = @Schema(implementation = URI.class), description = "The generated book's URI")})})
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<URI> createBook(@Valid @RequestBody Book book, HttpServletRequest httpServletRequest) {
        final var registerBook = bookService.registerBook(book);
        final var uri = URI.create(httpServletRequest.getRequestURL().append("/").append(registerBook.getId()).toString());
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Updates a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Books found",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Book.class))})})
    @PutMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Book> updateBook(@Valid @RequestBody Book book) {
        return ResponseEntity.ok(bookService.updateBook(book));
    }

    @Operation(summary = "Removes a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book removed")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@NotNull @PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
