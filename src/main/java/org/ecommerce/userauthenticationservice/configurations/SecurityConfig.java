package org.ecommerce.userauthenticationservice.configurations;

import org.springframework.boot.autoconfigure.http.client.HttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final HttpClientProperties httpClientProperties;

    public SecurityConfig(HttpClientProperties httpClientProperties) {
        this.httpClientProperties = httpClientProperties;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().disable();
        httpSecurity.csrf().disable();
        httpSecurity.authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll());
        return httpSecurity.build();
    }

}
