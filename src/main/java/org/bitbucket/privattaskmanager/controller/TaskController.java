package org.bitbucket.privattaskmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.bitbucket.privattaskmanager.domain.Task;
import org.bitbucket.privattaskmanager.service.TaskServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskServiceImpl taskService;

    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Create a new task")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return new ResponseEntity<>(taskService.createTask(task), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete task by ID")
    @DeleteMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        boolean isDeleted = taskService.deleteTask(id);

        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Update task status")
    @PutMapping(value = "status/{id}")
    public ResponseEntity<String> updateTaskStatus(@PathVariable Long id, @RequestParam String statusStr) {
        if (statusStr == null || statusStr.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isUpdated = taskService.updateTaskStatus(id, statusStr);
        if (isUpdated) {
            return ResponseEntity.ok("Status updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        }
    }

    @Operation(summary = "Update task by ID")
    @PatchMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateTask(@PathVariable Long id, @RequestBody Task task) {
        boolean isUpdated = taskService.updateTask(id, task);
        if (isUpdated) {
            return ResponseEntity.ok("Status updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        }
    }

    @Operation(summary = "Find All tasks")
    @GetMapping(value = "all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.findAll();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
}
