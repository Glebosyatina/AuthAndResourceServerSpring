package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

//контроллер для регистрации
@RestController
@RequestMapping("/api/clients")
public class UserController {

  private final RegisteredClientRepository registeredClientRepository;

  @Autowired
  public UserController(RegisteredClientRepository registeredClientRepository){
    this.registeredClientRepository = registeredClientRepository;
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerClient(
      @RequestParam("client_id") String client_id,
      @RequestParam("client_secret") String client_secret,
      @RequestParam("grant_type") String grant_type
  ) {
    //проверка на верный  grant_type
    if (!"client_credentials".equals(grant_type)) {
      return ResponseEntity.badRequest().body("Чак сказал что grant_type только 'client_credentials'.");
    }

    //проверяем что такого пользователя еще нет
    if (registeredClientRepository.findByClientId(client_id) != null) {
      return ResponseEntity.badRequest().body("Чак уже встречал такого, будь собой - не будь не собой.");
    }

    RegisteredClient client = RegisteredClient
        .withId(UUID.randomUUID().toString())
        .clientId(client_id)
        .clientSecret("{noop}"+client_secret)
        .clientName("Юзер")
        .scope("write")
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
        .build();

    registeredClientRepository.save(client);

    return ResponseEntity.ok("Поздравляю, Чак тебя запомнил");
  }
}
