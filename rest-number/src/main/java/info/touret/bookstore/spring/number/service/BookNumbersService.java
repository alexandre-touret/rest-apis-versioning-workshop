package info.touret.bookstore.spring.number.service;

import info.touret.bookstore.spring.number.exception.ISBNExecutionException;
import info.touret.bookstore.spring.number.generated.dto.BookNumbersDto;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.random.RandomGeneratorFactory;

@Service
public class BookNumbersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookNumbersService.class);


    @Value("${number.separator:false}")
    private boolean separator;

    @Value("${time.to.sleep:15}")
    private int timeToSleep;

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("JJS => timeToSleep= {}", timeToSleep);
    }

    /**
     * Just a wrapper to the {@link #createBookNumbers()} method for applying Timeout handling with Resilience4J
     * If there is a timeout calling it, the method {@link #generateBookNumbersFallBack(TimeoutException)} is called
     * The timeout mechanism is specified in the <code>application.yml</code> . You can check the <code>book-numbers</code> timeout instance out.
     *
     * @return The BookNumbers DTO asynchronously
     */
    @TimeLimiter(name = "book-numbers", fallbackMethod = "generateBookNumbersFallBack")
    public CompletableFuture<BookNumbersDto> createBookNumbersAsync() {
        return CompletableFuture.supplyAsync(this::createBookNumbers);
    }

    @SuppressWarnings("java:S2142")
    public BookNumbersDto createBookNumbers() {
        LOGGER.info("Generating book numbers, sleeping {} msec", timeToSleep);

        try {
            if (timeToSleep != 0)
                TimeUnit.MILLISECONDS.sleep(timeToSleep);
        } catch (InterruptedException e) {
            throw new ISBNExecutionException(e);
        }

        var randomGenerator = RandomGeneratorFactory.getDefault().create();
        var random = Random.from(randomGenerator);
        BookNumbersDto bookNumbers = new BookNumbersDto();
        bookNumbers.setIsbn10(String.valueOf(random.nextLong(1_000_000_000,9_999_999_999L)));
        bookNumbers.setIsbn13(String.valueOf(random.nextLong(1000_000_000_000L,9999_999_999_999L)));
        bookNumbers.setAsin(String.valueOf(random.nextLong(1_000_000_000,9_999_999_999L)));
        bookNumbers.setEan8(String.valueOf(random.nextLong(1_000_000_0,9_999_999_9L)));
        bookNumbers.setEan13(String.valueOf(random.nextLong(1000_000_000_000L,9999_999_999_999L)));
        return bookNumbers;
    }

    /**
     * Fallback method
     *
     * @param e The handled exception
     * @return failedFuture
     */
    public CompletableFuture<BookNumbersDto> generateBookNumbersFallBack(TimeoutException e) {
        LOGGER.error(e.getMessage(), e);
        return CompletableFuture.failedFuture(e);
    }
}
