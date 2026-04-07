package org.sid.apigateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${app.security.public-paths:/auth-service/auth/**,/actuator/**}")
    private String publicPathsRaw;

    @Value("${app.security.admin-paths:/user-service/users/admin/**}")
    private String adminPathsRaw;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        List<String> publicPaths = splitPatterns(publicPathsRaw);
        List<String> adminPaths = splitPatterns(adminPathsRaw);
        if (matchesAny(path, publicPaths)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        if (!jwtService.isTokenValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        if (matchesAny(path, adminPaths)) {
            List<String> roles = jwtService.extractRoles(token);
            if (roles.stream().noneMatch("ROLE_ADMIN"::equals)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }

    private boolean matchesAny(String path, List<String> patterns) {
        PathContainer pathContainer = PathContainer.parsePath(path);
        String normalized = pathContainer.value();
        return patterns != null && patterns.stream().anyMatch(pattern -> pathMatcher.match(pattern, normalized));
    }

    private List<String> splitPatterns(String value) {
        if (!StringUtils.hasText(value)) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
