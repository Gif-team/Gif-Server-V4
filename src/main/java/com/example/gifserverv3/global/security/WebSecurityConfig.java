package com.example.gifserverv3.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/refresh")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/post/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/badge/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/chat/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/s3/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/image/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/chat/**")).permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .anyRequest().authenticated()
        );

        // CORS 설정
        http.cors((cors) -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트엔드 도메인 허용 (localhost, 배포된 프론트엔드 도메인)
        configuration.setAllowedOrigins(Arrays.asList("https://www.gsmitemfind.com"));

        // 모든 헤더 허용
        configuration.addAllowedHeader("*");

        // 인증 관련 헤더 노출 (세션 기반에서도 필요)
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Refresh-Token");

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));

        // 자격 증명 포함 (세션 기반 인증을 위해 쿠키 허용)
        configuration.setAllowCredentials(true);

        // CORS 설정 경로
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
