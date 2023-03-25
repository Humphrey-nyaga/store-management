package com.example.storemanagement.security;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;

@Configuration
public class RateLimitConfig {

    private final RateLimitProperties properties;

    public RateLimitConfig(RateLimitProperties properties) {
        this.properties = properties;
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
    }

    @Bean
    public Map<String, AtomicInteger> rateLimiter() {
        return new ConcurrentHashMap<>();
    }

    /*

    @Bean
    public WebFilter rateLimitFilter(Map<String, AtomicInteger> rateLimiter) {
        return (exchange, chain) -> {
            String key = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            AtomicInteger count = rateLimiter.computeIfAbsent(key, k -> new AtomicInteger(0));
            if (count.incrementAndGet() > properties.getLimit()) {
                return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));
            }
            return chain.filter(exchange).doFinally(signal -> {
                if (signal != SignalType.ON_ERROR) {
                    count.decrementAndGet();
                }
            });
        };
    }*/

    @Bean
    public WebFilter rateLimitFilter(Map<String, AtomicInteger> rateLimiter, RateLimitProperties rateLimitProperties) {
        int capacity = rateLimitProperties.getCapacity();
        Duration interval = Duration.parse("PT" + rateLimitProperties.getInterval().toUpperCase());
        Semaphore semaphore = new Semaphore(capacity);

        return (exchange, chain) -> {
            String key = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            AtomicInteger count = rateLimiter.computeIfAbsent(key, k -> new AtomicInteger(0));
            if (count.incrementAndGet() > capacity) {
                return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));
            }
            try {
                if (!semaphore.tryAcquire(interval.toMillis(), TimeUnit.MILLISECONDS)) {
                    return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));
                }
            } catch (InterruptedException e) {
                return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred"));
            }
            return chain.filter(exchange).doFinally(signal -> {
                if (signal != SignalType.ON_ERROR) {
                    count.decrementAndGet();
                    semaphore.release();
                }
            });
        };
    }



}



