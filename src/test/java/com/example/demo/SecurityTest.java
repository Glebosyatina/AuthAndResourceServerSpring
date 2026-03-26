package com.example.demo;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.example.demo.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    private static final String GET_ACCESS_TOKEN_ENDPOINT = "/oauth2/token"; //в Spring Auth Server по умолчанию этот endpoint для получения jwt токена
    private static final String PRODUCT_ENDPOINT = "/api/tasks";


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


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

    //тест с правильными кредами
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


    //тестируем доступ к ресурсам
    @Test
    public void testGetListOfTasksWithScopeRead() throws Exception {

        mockMvc.perform(get(PRODUCT_ENDPOINT)
                    .with(user("client2"))
                    .with(jwt().jwt(jwt -> jwt.claim("scope", "read")))
                )
                .andDo(print());
    }

    //не должен давать создавать со scope read
    @Test
    public void testAddTaskWithScopeRead() throws Exception {

        Task task = new Task("сходить в магазин");
        //сериализация продукта в строку
        String requestBody = objectMapper.writeValueAsString(task);

        mockMvc.perform(post(PRODUCT_ENDPOINT)
                    .contentType("application/json")
                    .content(requestBody)
                    .with(jwt().jwt(jwt -> jwt.claim("scope", "read")))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    //scope write пропускаем и даем создать продукт
    @Test
    public void testAddTaskWithScopeWrite() throws Exception {

        Task task = new Task("client1", "сходить в магазин");

        //сериализация продукта в строку
        String requestBody = objectMapper.writeValueAsString(task);

        mockMvc.perform(post(PRODUCT_ENDPOINT)
                    .contentType("application/json")
                    .content(requestBody)
                    .with(jwt().jwt(jwt -> jwt.claim("scope", "write")))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

}
