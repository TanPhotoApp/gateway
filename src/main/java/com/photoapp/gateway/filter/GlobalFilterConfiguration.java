package com.photoapp.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class GlobalFilterConfiguration {

    @Order(1)
    @Bean
    public GlobalFilter secondFilter() {
        return (exchange, chain) -> {
            log.info("Second pre-filter run");
            return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    log.info("Second post-filter run");
                }));
        };
    }

    @Order(2)
    @Bean
    public GlobalFilter thirdFilter() {
        return (exchange, chain) -> {
            log.info("Third pre-filter run");
            return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    log.info("Third post-filter run");
                }));
        };
    }

}
