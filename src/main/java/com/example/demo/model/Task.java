package com.example.demo.model;

import jakarta.persistence.*;

import java.util.UUID;


@Entity
@Table(name="tasks")
@IdClass(UserId.class)
public class Task {

  @Id
  @Column(name = "username")
  private String user; //к какому пользователю относится таска

  @Id
  @Column(name = "id")
  private UUID id;

  private String content;

  //конструктор без параметоров, нужен spring для создания объектов при сериализации/десериализации из json
  public Task(){}

  public Task(String user, String content){
    this.user = user;
    this.content = content;
  }

  public Task(String content){
    this.content = content;
  }

  //геттеры сеттеры
  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
