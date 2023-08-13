package com.example.todos.controller;

import com.example.todos.model.Todo;
import com.example.todos.repository.TodoRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

  @Autowired
  private final TodoRepository todoRepository;

  @GetMapping
  public List<Todo> getAllTodos() {
    return todoRepository.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
    Optional<Todo> todo = todoRepository.findById(id);
    return todo.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
    Todo savedTodo = todoRepository.save(todo);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedTodo);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo updatedTodo) {
    Optional<Todo> todo = todoRepository.findById(id);
    return todo.map(existingTodo -> {
      existingTodo.setTitle(updatedTodo.getTitle());
      existingTodo.setDescription(updatedTodo.getDescription());
      existingTodo.setCompleted(updatedTodo.isCompleted());
      todoRepository.save(existingTodo);
      return ResponseEntity.ok(existingTodo);
    }).orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
    Optional<Todo> todo = todoRepository.findById(id);
    if (todo.isPresent()) {
      todoRepository.delete(todo.get());
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}

