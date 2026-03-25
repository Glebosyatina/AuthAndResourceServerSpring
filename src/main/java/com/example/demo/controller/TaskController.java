package com.example.demo.controller;


import com.example.demo.model.Task;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class TaskController {

  //in-memory store key - username, value - list tasks
  private static final Map<String,List<Task>> tasksMap = new HashMap<>();

  @GetMapping("/tasks")
  public ResponseEntity<?> getTasks() {
    //достаем пользователя из jwt bearear токена
    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    List<Task> listTasks = tasksMap.get(user);
    return ResponseEntity.ok(listTasks);
  }

  @GetMapping("/tasks/{id}")
  public ResponseEntity<?> getTask(@PathVariable Integer id) {

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    List<Task> listTasks = tasksMap.get(user);
    Optional<Task> result = listTasks.stream().filter(p -> Objects.equals(p.getId(), id)).findFirst();
    return ResponseEntity.ok(result);
  }

  @PostMapping("/tasks")
  public ResponseEntity<?> createTask(@RequestBody Task task) {

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    List<Task> taskList = tasksMap.get(user);
    if (taskList == null){
      taskList = new ArrayList<>();
      tasksMap.put(user, taskList);
    }
    //установка id и user_id для задачки
    task.setId(taskList.size());
    task.setUser_id(user);
    taskList.add(task);

    return ResponseEntity.ok(task);
  }

  @PutMapping("/tasks/{id}")
  public ResponseEntity<?> updateTask(@PathVariable Integer id, @RequestBody Task taskNew) {

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    List<Task> taskList = tasksMap.get(user);
    for (int i = 0; i < taskList.size(); i++){
      if (Objects.equals(taskList.get(i).getId(), id)){
        taskNew.setId(i);
        taskNew.setUser_id(user);
        taskList.set(i, taskNew);
        break;
      }
    }

    return ResponseEntity.ok(taskNew);
  }

  @DeleteMapping("/tasks/{id}")
  public ResponseEntity<?> deleteTask(@PathVariable Integer id) {

    String user = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    List<Task> taskList = tasksMap.get(user);

    //поиск задачи для удаления
    boolean removed = taskList.removeIf(task -> Objects.equals(task.getId(), id));
    if (removed){
      //обновление все id
      for (int i = 0; i < taskList.size(); i++){
        taskList.get(i).setId(i);
      }
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.notFound().build();

  }

}
