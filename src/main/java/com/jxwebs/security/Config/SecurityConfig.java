package com.jxwebs.security.Config;

import com.jxwebs.security.Comment.JsonWebTokenUtility;
import com.jxwebs.security.Filter.JwtAuthenticationFilter;
import com.jxwebs.security.Filter.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JsonWebTokenUtility jwtUtility;
    private final ReactiveUserDetailsService userDetailsService;


    public SecurityConfig(JsonWebTokenUtility jwtUtility, ReactiveUserDetailsService userDetailsService) {
        this.jwtUtility = jwtUtility;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // 禁用表單登入
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/login","/adduser").permitAll()
                        .pathMatchers("/test").hasAnyAuthority("user","boss")
                        .pathMatchers("/testboss").hasAuthority("boss")
                        .anyExchange().authenticated()
                )
                // 認證Filter放在AUTHENTICATION位置
                .addFilterAt(new JwtAuthenticationFilter(jwtUtility, userDetailsService),
                        SecurityWebFiltersOrder.AUTHENTICATION)
                // 授權Filter放在AUTHORIZATION位置
//                .addFilterAt(new JwtAuthorizationFilter(jwtUtility, userDetailsService),
//                        SecurityWebFiltersOrder.AUTHORIZATION)
                .build();
    }
}