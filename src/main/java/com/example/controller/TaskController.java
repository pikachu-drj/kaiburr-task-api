package com.example.controller;

import com.example.model.Task;
import com.example.model.TaskExecution; // Make sure this import is added
import com.example.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Make sure this import is added
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date; // Make sure this import is added
import java.util.ArrayList; // Make sure this import is added
import java.util.List;
import java.util.Optional; // Make sure this import is added

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    // Endpoint to create or update a task
    @PutMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        // Simple security check for malicious commands
        if (task.getCommand() != null && (task.getCommand().contains("rm -rf") || task.getCommand().contains("sudo"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Return 400 Bad Request
        }
        Task savedTask = taskRepository.save(task);
        return ResponseEntity.ok(savedTask);
    }

    // Endpoint to get all tasks
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Endpoint to get a single task by ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        return taskRepository.findById(id)
                .map(task -> ResponseEntity.ok().body(task))
                .orElse(ResponseEntity.notFound().build());
    }

    // NEW: Endpoint to find tasks by name [cite: 74]
    @GetMapping("/find")
    public ResponseEntity<List<Task>> findTasksByName(@RequestParam String name) {
        List<Task> tasks = taskRepository.findByNameContaining(name);
        if (tasks.isEmpty()) {
            return ResponseEntity.notFound().build(); // Return 404 if nothing is found [cite: 76]
        }
        return ResponseEntity.ok(tasks);
    }

    // NEW: Endpoint to delete a task by ID [cite: 73]
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        if (!taskRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); // Return 404 if the task doesn't exist
        }
        taskRepository.deleteById(id);
        return ResponseEntity.ok().build(); // Return 200 OK on successful deletion
    }

    // NEW: Endpoint to "execute" a task [cite: 77]
    @PutMapping("/{id}/execute")
    public ResponseEntity<Task> executeTask(@PathVariable String id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Task task = taskOptional.get();

        // Create a new execution record
        TaskExecution execution = new TaskExecution();
        execution.setStartTime(new Date());

        // For Task 1, we just simulate the output.
        // In Task 2, this is where you'll run the command in Kubernetes.
        execution.setOutput("Simulated output for command: " + task.getCommand());
        execution.setEndTime(new Date());

        // Add the new execution to the task's history
        if (task.getTaskExecutions() == null) {
            task.setTaskExecutions(new ArrayList<>());
        }
        task.getTaskExecutions().add(execution);

        // Save the updated task back to the database
        Task updatedTask = taskRepository.save(task);
        return ResponseEntity.ok(updatedTask);
    }
}