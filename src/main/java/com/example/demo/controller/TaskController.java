package com.example.demo.controller;


import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5000")
public class TaskController {

  //зависимости
  private final TaskService taskService;

  //autowired для автоматического инжекта зависимостей
  @Autowired
  public TaskController(TaskService taskService){
    this.taskService = taskService;
  }


  //все задачи
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

  //конкретная задача
  @GetMapping("/tasks/{id}")
  public ResponseEntity<?> getTask(
      @PathVariable UUID id
  ) {

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    Task task = taskService.getTaskById(user, id);
    if (task == null){
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(task);
  }

  //создаем задачу
  @PostMapping("/tasks")
  public ResponseEntity<?> createTask(
      @RequestBody Task task
  ) {

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    Task created = taskService.createTask(user, task);
    if (created == null){
      return ResponseEntity.internalServerError().build();
    }
    return ResponseEntity.created(URI.create("/api/tasks")).body(created);
  }

  //изменяем задачу
  @PutMapping("/tasks/{id}")
  public ResponseEntity<?> updateTask(
      @PathVariable UUID id,
      @RequestBody Task taskNew
  ) {

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    return ResponseEntity.ok(taskService.updateTask(user, id, taskNew));
  }

  //удаляем задачу
  @DeleteMapping("/tasks/{id}")
  public ResponseEntity<?> deleteTask(
      @PathVariable UUID id
  ) {

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    if (taskService.deleteTask(user, id)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.notFound().build();
  }


   //задачи по дате
  @GetMapping("/tasks/by-date")
  public ResponseEntity<?> getTasksByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date){

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    return ResponseEntity.ok(taskService.getTasksByDate(user, date));
  }

   //просроченные задачи
  @GetMapping("/tasks/overdue")
  public ResponseEntity<?> getOverdueTasks(){

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    return ResponseEntity.ok(taskService.getOverdueTasks(user));
  }

  //выполненные задачи
  @GetMapping("/tasks/completed")
  public ResponseEntity<?> getCompletedTasks(){

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    return ResponseEntity.ok(taskService.getCompletedTasks(user));
  }



}
