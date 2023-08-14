package com.basic.orderapi.config;

import com.basic.domain.config.JwtAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider()
    {
        return new JwtAuthenticationProvider(); //객체 소멸과 생성에 관하여 공부해보기
    }
}
