package info.touret.bookstore.spring.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    /**
     * Defines ABAC based security policy <br/>
     * CSRF and CORS are disabled for testing purpose only ;-)
     *
     * @param http the HTTP Security configuration
     * @return
     */
    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        /* Defaut configuration for OAUTH authorization (TO BE ADDED during the workshop)

        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(GET, "/v1/books/count").hasAuthority("SCOPE_bookv1:read")
                        .pathMatchers(GET, "/v1/books/random").hasAuthority("SCOPE_bookv1:read")
                        .pathMatchers(POST, "/v1/books").hasAuthority("SCOPE_bookv1:write")
                        .pathMatchers(GET, "/v1/books").hasAuthority("SCOPE_bookv1:read")
                        .pathMatchers("/v1/isbns").hasAuthority("SCOPE_numberv1:read")
                        .pathMatchers(GET, "/v2/books/count").hasAuthority("SCOPE_bookv2:read")
                        .pathMatchers(GET, "/v2/books/random").hasAuthority("SCOPE_bookv2:read")
                        .pathMatchers(POST, "/v2/books").hasAuthority("SCOPE_bookv2:write")
                        .pathMatchers(GET, "/v2/books").hasAuthority("SCOPE_bookv2:read")
                        .pathMatchers("/v2/isbns").hasAuthority("SCOPE_numberv2:read")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(Customizer.withDefaults()));*/
        /* If the previous configuration is applied, you would remove this following line (and the other way around)*/
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll());
        return http.build();
    }

    /* If the security is enabled, you MUST uncomment the following factories
    @Bean
    JwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties) {
        return NimbusJwtDecoder.withJwkSetUri(properties.getJwt().getJwkSetUri()).build();

    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String issuerUrl) {
        return ReactiveJwtDecoders.fromIssuerLocation(issuerUrl);
    } */
}
