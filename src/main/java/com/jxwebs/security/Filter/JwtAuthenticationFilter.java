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

public class JwtAuthenticationFilter implements WebFilter {

    private final JsonWebTokenUtility jwtUtility;
    private final ReactiveUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JsonWebTokenUtility jwtUtility, ReactiveUserDetailsService userDetailsService) {
        this.jwtUtility = jwtUtility;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 验证 Token 并提取用户名和权限
            return jwtUtility.validateToken(token)
                    .flatMap(claims -> {
                        if (claims != null) {
                            String username = claims[0];
                            // 异步加载用户详情
                            return userDetailsService.findByUsername(username)
                                    .flatMap(userDetails -> {
                                        UsernamePasswordAuthenticationToken authenticationToken =
                                                new UsernamePasswordAuthenticationToken(
                                                        userDetails,
                                                        null,
                                                        userDetails.getAuthorities()
                                                );

                                        // 创建 SecurityContext
                                        SecurityContext securityContext = new SecurityContextImpl(authenticationToken);

                                        // 将 SecurityContext 设置到上下文中
                                        return chain.filter(exchange)
                                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                                    });
                        }
                        return Mono.empty(); // 无效 Token
                    });
        }
        System.out.println("无 Authorization 头");
        // 无 Authorization 头，直接通过过滤器链
        return chain.filter(exchange);
    }
}