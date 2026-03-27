package com.example.demo.model;

import java.io.Serializable;
import java.util.UUID;

//составной primary key из юзера и id задачи
public class UserId implements Serializable {
  protected String user;
  protected UUID id;

  public UserId(){}

  public UserId(String user,  UUID id){
    this.user = user;
    this.id = id;
  }
}
