package com.example.demo;

import com.example.demo.model.Task;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    private static final String REGISTER_ENDPOINT = "/api/clients/register";
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

    //создание задачи, пока даем создавать с любым scope не важно read или write
    @Test
    public void testAddTaskWithScopeRead() throws Exception {

        Task task = new Task("Чак Норрис победил солнце в состязании взглядов.");
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

    //даем создать задачу
    @Test
    public void testAddTaskWithScopeWrite() throws Exception {

        Task task = new Task("Chack Norris", "Чак Норрис заставил «Хэппи Мил» заплакать.");

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

    @Test
    public void testUpdateTask() throws Exception {

        Task task = new Task("Chack Norris", "Когда Чак режет лук - плачет лук");
        //сериализация продукта в строку
        String requestBody = objectMapper.writeValueAsString(task);

        MvcResult result = mockMvc.perform(post(PRODUCT_ENDPOINT)
                .contentType("application/json")
                .content(requestBody)
                .with(jwt().jwt(jwt -> jwt.claim("scope", "write")))
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn();

        //id созданной задачи
        String taskId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        //обновленная задача
        Task taskUpdate = new Task("Chack Norris", "Я думаю, Вы можете извлечь уроки из истории");
        //сериализация продукта в строку
        String requestBodyUpdate = objectMapper.writeValueAsString(taskUpdate);

        mockMvc.perform(put(PRODUCT_ENDPOINT+"/"+taskId)
                .contentType("application/json")
                .content(requestBodyUpdate)
                .with(jwt().jwt(jwt -> jwt.claim("scope", "write")))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void deleteTask() throws Exception {

        Task task = new Task("Chack Norris", "На седьмой день Бог отдыхал. Затем за дело взялся Чак Норрис");
        //сериализация продукта в строку
        String requestBody = objectMapper.writeValueAsString(task);

        MvcResult result = mockMvc.perform(post(PRODUCT_ENDPOINT)
                .contentType("application/json")
                .content(requestBody)
                .with(jwt().jwt(jwt -> jwt.claim("scope", "write")))
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn();

        //id созданной задачи
        String taskId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete(PRODUCT_ENDPOINT+"/"+taskId)
                .contentType("application/json")
                .with(jwt().jwt(jwt -> jwt.claim("scope", "write")))
            )
            .andDo(print())
            .andExpect(status().isOk());

        mockMvc.perform(get(PRODUCT_ENDPOINT+"/"+taskId)
                .contentType("application/json")
                .with(jwt().jwt(jwt -> jwt.claim("scope", "write")))
            )
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void registerClient() throws Exception {

        MvcResult result = mockMvc.perform(post(REGISTER_ENDPOINT)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content("client_id=Chuck&client_secret=Norris&grant_type=client_credentials")
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        assertEquals("Поздравляю, Чак тебя запомнил", result.getResponse().getContentAsString());

    }

}
