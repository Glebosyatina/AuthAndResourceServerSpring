package com.example.demo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

//сервер ресурсов
@Configuration
public class ResourceServerConfig {

    @Bean
    SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception{

        //фильтр чтобы пропускало только на то что начинается с /api
        http.securityMatcher("/api/**")

                //отключаем csrf
                .csrf(csrf -> csrf.disable())

                //каждый запрос должен нести полную информацию об аутентификации (JWT в заголовке Authorization)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))

                // на все запросы которые идут на /api/products требуем scope read или write в jwt токене
                //и проверяем что такой пользователь авторизован
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/tasks").hasAnyAuthority("SCOPE_read", "SCOPE_write")
                        .anyRequest().authenticated());

        return http.build();
    }

}
