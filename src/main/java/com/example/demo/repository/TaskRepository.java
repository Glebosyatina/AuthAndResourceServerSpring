package com.example.demo.repository;

import com.example.demo.model.Task;
import com.example.demo.model.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//репозиторный слой, инжектится в сервисный слой

//репозиторий управления задачами
@Repository
public interface TaskRepository extends JpaRepository<Task, UserId> {
  List<Task> findAllByUser(String user);
}
