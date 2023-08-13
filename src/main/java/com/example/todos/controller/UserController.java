package com.example.todos.controller;

import com.example.todos.model.User;
import com.example.todos.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @PostMapping("/register")
  public ResponseEntity<User> registerUser(@RequestBody User user) {
    Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
    if (existingUser.isPresent()) {
      return ResponseEntity.badRequest().body(null);
    }

    User savedUser = userRepository.save(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
  }

}