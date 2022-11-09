package info.touret.bookstore.spring.number.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import info.touret.bookstore.spring.number.dto.BookNumbers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.GATEWAY_TIMEOUT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(value = {"timeout", "default"})
class BookNumbersControllerTimeoutIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void should_get_book_numbers_fallback() {
        var response = restTemplate.getForEntity("http://127.0.0.1:" + port + "/isbns", BookNumbers.class);
        assertEquals(GATEWAY_TIMEOUT, response.getStatusCode());
    }
}
