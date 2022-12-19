package info.touret.bookstore.spring.book;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Book Spring Configuration
 */
@Configuration
public class BookConfiguration {

    @Value("${booknumbers.api.timeout_sec}")
    private int timeoutInSec;

    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

    @Bean
    public RestTemplate createRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    /**
     * Creates a circuit breaker customizer applying a timeout specified by the <code>booknumbers.api.timeout_sec</code> property.
     *
     * @return the default resilience4j circuit breaker customizer
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> createDefaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(timeoutInSec)).build())
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .build());
    }

    /**
     * Creates a circuit breaker customizer applying a timeout specified by the <code>booknumbers.api.timeout_sec</code> property.
     * This customizer could be reached using this id: <code>slowNumbers</code>
     * @return the circuit breaker customizer to apply when calling to numbers api
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> createSlowNumbersAPICallCustomizer() {
        return factory -> factory.configure(builder -> builder.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(timeoutInSec)).build()), "slowNumbers");
    }
}
