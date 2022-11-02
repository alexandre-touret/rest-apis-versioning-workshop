package info.touret.bookstore.spring.book.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.touret.bookstore.spring.book.BookConfiguration;
import info.touret.bookstore.spring.book.entity.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import info.touret.bookstore.spring.book.dto.IsbnNumbers;
import info.touret.bookstore.spring.book.exception.ApiCallTimeoutException;
import info.touret.bookstore.spring.book.repository.BookRepository;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * Book Spring Service
 */
@Service
@Validated
public class BookService {


    private static final Logger LOGGER = LoggerFactory.getLogger(BookService.class);
    private BookRepository bookRepository;
    private RestTemplate restTemplate;

    private CircuitBreakerFactory circuitBreakerFactory;
    private String isbnServiceURL;

    public BookService(BookRepository bookRepository,
                       RestTemplate restTemplate,
                       @Value("${booknumbers.api.url}") String isbnServiceURL,
                       @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") CircuitBreakerFactory circuitBreakerFactory) {
        this.bookRepository = bookRepository;
        this.restTemplate = restTemplate;
        this.isbnServiceURL = isbnServiceURL;

        this.circuitBreakerFactory = circuitBreakerFactory;

    }

    /**
     * Gets all the different IDS stored in the database and pick at random one book among these.
     *
     * @return A random book
     */
    public Book findRandomBook() {
        var ids = bookRepository.findAllIds();
        final var size = ids.size();
        var aLong = ids.get(new SecureRandom().nextInt(size));
        return findBookById(aLong).orElseThrow(IllegalStateException::new);
    }


    /**
     * Registers the book. If the underlying API is not reachable, a circuit breaker is applied to call <code>fallbackPersistBook</code>
     *
     * @param book book to register
     * @return the book saved
     * @see #fallbackPersistBook(Book)
     * @see BookConfiguration#createSlowNumbersAPICallCustomizer()
     */
    public Book registerBook(@Valid Book book) {
        circuitBreakerFactory.create("slowNumbers").run(
                () -> persistBook(book),
                throwable -> fallbackPersistBook(book)
        );

        return bookRepository.save(book);
    }

    /**
     * Fallback method used for serialising the payload into a JSON file
     *
     * @param book the current book
     * @return the current book
     * @throws IllegalStateException   can't store the current file
     * @throws ApiCallTimeoutException normal behaviour.
     */
    // We have no ISBN numbers, we cannot persist in the database
    private Book fallbackPersistBook(Book book) {
        try (var out = new PrintWriter("book-" + Instant.now().toEpochMilli() + ".json")) {
            var objectMapper = new ObjectMapper();
            var bookJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(book);
            out.println(bookJson);
        } catch (FileNotFoundException | JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalStateException("Cannot serialize data");
        }
        throw new ApiCallTimeoutException("Numbers not accessible");
    }

    /**
     * Finds all books
     *
     * @return all the books stored in the database
     */
    public List<Book> findAllBooks() {
        return StreamSupport.stream(bookRepository.findAll().spliterator(), false).collect(toList());
    }

    public long count() {
        return bookRepository.count();
    }

    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id);
    }

    // To be invistgated
    public Book updateBook(@Valid Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    private Book persistBook(Book book) {
        var isbnNumbers = restTemplate.getForEntity(isbnServiceURL, IsbnNumbers.class).getBody();
        if (isbnNumbers != null) {
            book.isbn13 = isbnNumbers.getIsbn13();
            book.isbn10 = isbnNumbers.getIsbn10();
        }
        return bookRepository.save(book);
    }
}
