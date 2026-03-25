package com.example.demo.model;

public class Task {

  private String user_id; //uniq primary key
  private Integer id;
  private String content;

  //конструктор без параметоров, нужен spring для создания объектов при сериализации/десериализации из json
  public Task(){}

  public Task(String user, String content){
    this.user_id = user;
    this.content = content;
  }

  //геттеры сеттеры
  public String getUser_id() {
    return user_id;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
