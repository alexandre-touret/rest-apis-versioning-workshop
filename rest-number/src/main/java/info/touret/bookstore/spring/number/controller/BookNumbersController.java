package info.touret.bookstore.spring.number.controller;

import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import info.touret.bookstore.spring.number.dto.BookNumbers;
import info.touret.bookstore.spring.number.service.BookNumbersService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Numbers API spring controller
 * the time to respond is monitor using <code>@Timed</code> annotation
 */
@Timed(value = "numberController")
@RestController
@RequestMapping(value = "/api/isbns", produces = APPLICATION_JSON_VALUE)
public class BookNumbersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookNumbersController.class);

    private BookNumbersService bookNumbersService;

    public BookNumbersController(BookNumbersService bookNumbersService) {
        this.bookNumbersService = bookNumbersService;
    }

    /**
     * Gets the ISBN numbers for a given book. If there is a timeout calling {@link #bookNumbersService}, the method {@link #generateBookNumbersFallBack(TimeoutException)} is called
     * The timeout mechanism is specified in the <code>application.yml</code> . You can check the <code>book-numbers</code> timeout instance out.
     *
     * @return The ISBN numbers
     * @see #generateBookNumbersFallBack(TimeoutException)
     */
    @TimeLimiter(name = "book-numbers", fallbackMethod = "generateBookNumbersFallBack")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Gets book numbers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book numbers",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookNumbers.class))}),
            @ApiResponse(responseCode = "504", description = "Timeout error",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookNumbers.class))})})
    public CompletableFuture<BookNumbers> generateBookNumbers() {
        return CompletableFuture.supplyAsync(() -> bookNumbersService.createBookNumbers());
    }

    /**
     * Fallback method
     * @param e The handled exception
     * @return failedFuture
     */
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    @ExceptionHandler({TimeoutException.class})
    public CompletableFuture<BookNumbers> generateBookNumbersFallBack(TimeoutException e) {
        LOGGER.error(e.getMessage(), e);
        return CompletableFuture.failedFuture(e);
    }
}
