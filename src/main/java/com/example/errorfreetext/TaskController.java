package com.example.errorfreetext;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Map<String, String> request) {
        String text = request.get("text");

        if (text == null || text.trim().length() < 3) {
            return ResponseEntity.badRequest().body(Map.of("error", "Текст должен содержать минимум 3 символа"));
        }

        if (!text.matches(".*[a-zA-Zа-яА-Я].*")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Текст не может содержать только цифры и спецсимволы"));
        }

        Task task = new Task();
        task.setOriginalText(text);
        Task savedTask = taskRepository.save(task);

        return ResponseEntity.ok(Map.of("id", savedTask.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task with id: " + id + " not found"));
        return ResponseEntity.ok(task);
    }
}