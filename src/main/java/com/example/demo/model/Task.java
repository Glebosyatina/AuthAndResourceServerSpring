package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
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

  @Column(name = "content")
  private String content;

  @Column(name = "completed")
  private boolean completed ;

  @Column(name = "due_date")
  private LocalDateTime dueDate;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  //конструктор без параметоров, нужен spring для создания объектов при сериализации/десериализации из json
  public Task(){}

  //если только юзер и содержание задачи, остальные по дефолту
  public Task(String user, String content){
    this.user = user;
    this.content = content;
    this.completed = false;
    this.dueDate = LocalDateTime.now();
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  //конструктор со всем параметрами, юзер, содержание, выполнена или нет, время дедлайна задачи, создания, обновления
  public Task(String user, String content, boolean completed, LocalDateTime dueDate, LocalDateTime createdAt, LocalDateTime updatedAt){
    this.user = user;
    this.content = content;
    this.completed = completed;
    this.dueDate = dueDate;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
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

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public LocalDateTime getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDateTime dueDate) {
    this.dueDate = dueDate;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
