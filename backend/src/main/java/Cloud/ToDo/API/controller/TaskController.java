package Cloud.ToDo.API.controller;

import Cloud.ToDo.API.entity.Priority;
import Cloud.ToDo.API.entity.Status;
import Cloud.ToDo.API.entity.Task;
import Cloud.ToDo.API.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public Page<Task> getAllTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        if (page < 0) {
            page = 0;
        }

        if (limit < 1) {
            limit = 10;
        }

        if (limit > 100) {
            limit = 100;
        }

        Pageable pageable = PageRequest.of(page, limit);

        if (status != null && priority != null) {
            return taskRepository.findByStatusAndPriority(
                    status,
                    priority,
                    pageable
            );
        }

        if (status != null) {
            return taskRepository.findByStatus(status, pageable);
        }

        if (priority != null) {
            return taskRepository.findByPriority(priority, pageable);
        }

        return taskRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        existing.setTitle(task.getTitle());
        existing.setDescription(task.getDescription());
        existing.setStatus(task.getStatus());
        existing.setPriority(task.getPriority());
        existing.setDueDate(task.getDueDate());

        return taskRepository.save(existing);
    }

    @PatchMapping("/{id}")
    public Task patchTask(
            @PathVariable Long id,
            @RequestBody Task task
    ) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getStatus() != null) {
            existing.setStatus(task.getStatus());
        }

        if (task.getPriority() != null) {
            existing.setPriority(task.getPriority());
        }

        return taskRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found");
        }

        taskRepository.deleteById(id);
    }
}