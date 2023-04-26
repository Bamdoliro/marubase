package com.bamdoliro.maru.shared.security;

import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import com.bamdoliro.maru.shared.filter.GlobalErrorFilter;
import com.bamdoliro.maru.shared.filter.JwtAuthenticationFilter;
import com.bamdoliro.maru.shared.security.auth.AuthDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProperties jwtProperties;
    private final TokenService tokenService;
    private final AuthDetailsService authDetailsService;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .apply(new CustomFilterChain())

                .and()
                .authorizeHttpRequests(request -> request
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/user/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    public class CustomFilterChain extends AbstractHttpConfigurer<CustomFilterChain, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) {
            http
                    .addFilterBefore(new JwtAuthenticationFilter(jwtProperties, tokenService, authDetailsService), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(new GlobalErrorFilter(objectMapper), JwtAuthenticationFilter.class);
        }
    }
}
