package com.jxwebs.security.Filter;

import com.jxwebs.security.Comment.JsonWebTokenUtility;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class JwtAuthorizationFilter implements WebFilter {

    private final JsonWebTokenUtility jwtUtility;
    private final ReactiveUserDetailsService userDetailsService;

    public JwtAuthorizationFilter(JsonWebTokenUtility jwtUtility, ReactiveUserDetailsService userDetailsService) {
        this.jwtUtility = jwtUtility;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            return jwtUtility.validateToken(token)
                    .flatMap(claims -> {
                        if (claims != null) {
                            String username = claims[0];
                            return userDetailsService.findByUsername(username)
                                    .flatMap(userDetails -> {
                                        UsernamePasswordAuthenticationToken authenticationToken =
                                                new UsernamePasswordAuthenticationToken(
                                                        userDetails,
                                                        null,
                                                        userDetails.getAuthorities()
                                                );

                                        SecurityContext context = new SecurityContextImpl(authenticationToken);
                                        return chain.filter(exchange)
                                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                                    })
                                    .switchIfEmpty(Mono.defer(() -> {
                                        return chain.filter(exchange);
                                    }));
                        }
                        return Mono.defer(() -> {
                            return chain.filter(exchange);
                        });
                    })
                    .onErrorResume(ex -> {
                        return Mono.defer(() -> {
                            return chain.filter(exchange);
                        });
                    });
        }
        return chain.filter(exchange);
    }
}
