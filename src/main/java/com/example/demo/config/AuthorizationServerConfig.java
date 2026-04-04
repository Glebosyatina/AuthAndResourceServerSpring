package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;


//сервер авторизации
@Configuration
public class AuthorizationServerConfig {

    private static final long ACCESS_TOKEN_EXPIRATION_TIME_MINUTES = 10;


    //выдача Jwt токена при авторизации по /oauth2/token
    @Bean
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) {
        //пропускаем только те что начинаются с /oath2/
        http.securityMatcher("/oauth2/**");
        http.authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .oauth2AuthorizationServer((authorizationServer) -> authorizationServer
                        .oidc(Customizer.withDefaults())
                );
        return http.build();
    }


    //имитируем зареганных пользователей
    @Bean
    RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {

        RegisteredClient client1 = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("client1")
                .clientSecret("{noop}123")
                .clientName("Gleb")
                .scope("read")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .build();

        RegisteredClient client2 = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("client2")
                .clientSecret("{noop}222")
                .clientName("Stepan")
                .scope("write")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .build();

        //создаем хранилище registeredClientRepository, теперь хранится в бд(см. resources/shema.sql)
        RegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);

        registeredClientRepository.save(client1);
        registeredClientRepository.save(client2);

        return registeredClientRepository;
    }

    //чтобы в Jwt токене передавать scope кастомизируем его
    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return (context) -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                RegisteredClient client = context.getRegisteredClient();

                JwtClaimsSet.Builder builder = context.getClaims();

                builder.issuer("CodeJava.net");
                builder.expiresAt(Instant.now().plus(ACCESS_TOKEN_EXPIRATION_TIME_MINUTES, ChronoUnit.MINUTES));

                builder.claims((claims) -> {
                    claims.put("scope", client.getScopes());
                });
            }
        };
    }

    //фильтр для регистрации

    //ручка для регистрации
    @Bean
    public SecurityFilterChain publicApiFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/clients/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .formLogin(Customizer.withDefaults());
        return http.build();
    }



    //CORS конфиг чтобы фронтенд мог отправлять запросы на бекенд
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5166")); //blazor url
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(360L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
