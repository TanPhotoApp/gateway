package com.photoapp.gateway.route.filter;

import io.jsonwebtoken.Jwts;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@RefreshScope
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Value("${token.signingKey}")
    private String signingKey;

    public static class Config {

    }

    /**
     * This constructor is required to let AbstractGatewayFilterFactory know which config class to cast
     * Missing this constructor will produce ClassCastException when start application
     */
    public AuthorizationHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var validToken = Optional.ofNullable(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION))
                .map(authHeader -> authHeader.get(0))
                .map(authHeader -> authHeader.replace("Bearer ", ""))
                .map(this::isValidToken)
                .orElse(false);

            if (!validToken) {
                return onError(exchange);
            }

            return chain.filter(exchange);
        };
    }

    private boolean isValidToken(String token) {
        try {
            var subject = Jwts.parserBuilder()
                .setSigningKey(signingKey.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

            return StringUtils.isNotEmpty(subject);
        } catch (Exception ex) {
            return false;
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

}
