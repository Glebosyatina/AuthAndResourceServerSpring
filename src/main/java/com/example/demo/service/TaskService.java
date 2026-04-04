package com.example.demo.service;

import com.example.demo.model.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

//слой бизнес логики, инжектится в контроллер
public interface TaskService {

  //CRUDL методы для задач\
  Task createTask(String user, Task task);
  List<Task> getTasks(String user);
  Task getTaskById(String user, UUID id);
  Task updateTask(String user, UUID id, Task task);
  boolean deleteTask(String user, UUID id);
  List<Task> getTasksByDate(LocalDateTime date);
  List<Task> getOverdueTasks();
}
