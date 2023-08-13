package com.example.todos.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.todos.model.Todo;
import com.example.todos.repository.TodoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TodoRepository todoRepository;

  private List<Todo> mockTodos;

  @BeforeEach
  public void setup() {
    mockTodos = new ArrayList<>();
    mockTodos.add(Todo.builder()
            .id(1L)
            .title("Task 1")
            .description("Description 1")
            .completed(false)
        .build());
    mockTodos.add(Todo.builder()
        .id(1L)
        .title("Task 2")
        .description("Description 2")
        .completed(true)
        .build());
  }

  @Test
  public void testGetAllTodos() throws Exception {
    when(todoRepository.findAll()).thenReturn(mockTodos);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/todos"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", is("Task 1")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].title", is("Task 2")));
  }

  @Test
  public void testGetTodoById() throws Exception {
    when(todoRepository.findById(anyLong())).thenReturn(Optional.of(mockTodos.get(0)));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/todos/1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.title", is("Task 1")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.completed", is(false)));
  }

  @Test
  public void testCreateTodo() throws Exception {

    Todo newTodo = Todo.builder()
        .id(null)
        .title("New Task")
        .description("New Description")
        .completed(false)
        .build();
    Todo savedTodo = Todo.builder()
        .id(3L)
        .title("New Task")
        .description("New Description")
        .completed(false)
        .build();

    when(todoRepository.save(any())).thenReturn(savedTodo);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\":\"New Task\",\"description\":\"New Description\",\"completed\":false}"))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(3)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.title", is("New Task")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.description", is("New Description")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.completed", is(false)));
  }

  @Test
  public void testUpdateTodoWithBuilder() throws Exception {
    Todo existingTodo = Todo.builder()
        .id(1L)
        .title("Existing Task")
        .description("Existing Description")
        .completed(false)
        .build();
    Todo updatedTodo = Todo.builder()
        .id(1L)
        .title("Updated Task")
        .description("Updated Description")
        .completed(true)
        .build();

    when(todoRepository.findById(anyLong())).thenReturn(Optional.of(existingTodo));
    when(todoRepository.save(any())).thenReturn(updatedTodo);

    mockMvc.perform(MockMvcRequestBuilders.put("/api/todos/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\":\"Updated Task\",\"description\":\"Updated Description\",\"completed\":true}"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.title", is("Updated Task")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.description", is("Updated Description")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.completed", is(true)));
  }

  @Test
  public void testDeleteTodoWithBuilder() throws Exception {
    Todo existingTodo = Todo.builder()
        .id(1L)
        .title("Task to Delete")
        .description("Description to Delete")
        .completed(false)
        .build();

    when(todoRepository.findById(anyLong())).thenReturn(Optional.of(existingTodo));

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/todos/1"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());

    verify(todoRepository, times(1)).delete(existingTodo);
  }
}
