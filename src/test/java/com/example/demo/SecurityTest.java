package com.example.demo;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {
    private static final String GET_ACCESS_TOKEN_ENDPOINT = "/oauth2/token";

    @Autowired
    MockMvc mockMvc;


    //тест с неправильными credentials, пользователь не получит токен
    @Test
    public void testGetAccessTokenFail() throws Exception {
        //выполнения запроса с неверными кредами
        mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
                .param("client_id", "123")
                .param("client_secret", "123")
                .param("grant_type", "client_credentials")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("invalid_client")))
                .andDo(print());
    }

    @Test
    public void testGetAccessTokenSuccess() throws Exception{
        //выполнения запроса с верными кредами, пользователся создавали в RegisteredClientRepository в AuthorizationServerConfig
        mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
                        .param("client_id", "client1")
                        .param("client_secret", "123")
                        .param("grant_type", "client_credentials")
                )
                .andExpect(status().isOk())                  //код ответа 200
                .andExpect(jsonPath("$.access_token").isString()) //парсим response body проверяя нужные поля
                .andExpect(jsonPath("$.expires_in").isNumber())
                .andExpect(jsonPath("$.token_type", is("Bearer")))
                .andDo(print());
    }
}
