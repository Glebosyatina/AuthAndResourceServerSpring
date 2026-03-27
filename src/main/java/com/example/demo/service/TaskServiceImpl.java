package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.model.UserId;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService{

  private final TaskRepository taskRepository;

  //автоматически инжектим зависимости
  @Autowired
  public TaskServiceImpl(TaskRepository taskRepository){
    this.taskRepository = taskRepository;
  }

  //реализуем методы сервиса
  @Override
  public Task createTask(String user, Task task) {

    task.setUser(user);
    //uuid задачи из пользователя и текста задача
    task.setId(UUID.nameUUIDFromBytes((user + task.getContent()).getBytes()));
    taskRepository.save(task);
    return task;
  }

  @Override
  public List<Task> getTasks(String user) {
    return taskRepository.findAllByUser(user);
  }

  @Override
  public Task getTaskById(String user, UUID id) {

    Optional<Task> task = taskRepository.findById(new UserId(user, id));
    //проверяем есть ли такая задача
    return task.orElse(null);
  }

  @Override
  public Task updateTask(String user, UUID id, Task task) {

    //находим старую версию задачи
    Optional<Task> taskOptional = taskRepository.findById(new UserId(user, id));
    //и меняем ее
    if (taskOptional.isPresent()){
      Task taskNew = taskOptional.get();
      taskNew.setContent(task.getContent());
      taskRepository.save(taskNew);
      return taskNew;
    }
    return null;
  }

  @Override
  public boolean deleteTask(String user, UUID id) {

    Optional<Task> task = taskRepository.findById(new UserId(user, id));
    //проверяем есть ли такая задача
    if (task.isEmpty()){
      return false;
    }

    taskRepository.deleteById(new UserId(user,id));
    return true;
  }
}
