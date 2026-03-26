package com.example.demo.controller;


import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api")
public class TaskController {

  //зависимости
  private final TaskService taskService;

  //autowired для автоматического инжекта зависимостей
  @Autowired
  public TaskController(TaskService taskService){
    this.taskService = taskService;
  }

  @GetMapping("/tasks")
  public ResponseEntity<?> getTasks() {
    //достаем пользователя из jwt bearear токена
    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    List<Task> tasks = taskService.getTasks(user);
    if (tasks.isEmpty()){
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(tasks);
  }

  @GetMapping("/tasks/{id}")
  public ResponseEntity<?> getTask(@PathVariable UUID id) {

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    return ResponseEntity.ok(taskService.getTaskById(user, id));
  }

  @PostMapping("/tasks")
  public ResponseEntity<?> createTask(@RequestBody Task task) {

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    Task created = taskService.createTask(user, task);
    if (created == null){
      return ResponseEntity.internalServerError().build();
    }
    return ResponseEntity.created(URI.create("/api/tasks")).build();
  }

  @PutMapping("/tasks/{id}")
  public ResponseEntity<?> updateTask(@PathVariable UUID id, @RequestBody Task taskNew) {

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    return ResponseEntity.ok(taskService.updateTask(user, id, taskNew));
  }

  @DeleteMapping("/tasks/{id}")
  public ResponseEntity<?> deleteTask(@PathVariable UUID id) {

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    taskService.deleteTask(user, id);
    return ResponseEntity.ok().build();
  }

}
