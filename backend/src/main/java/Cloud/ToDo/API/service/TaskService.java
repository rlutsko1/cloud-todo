package Cloud.ToDo.API.service;

import Cloud.ToDo.API.dto.TaskPatchRequest;
import Cloud.ToDo.API.dto.TaskRequest;
import Cloud.ToDo.API.entity.Priority;
import Cloud.ToDo.API.entity.Status;
import Cloud.ToDo.API.entity.Task;
import Cloud.ToDo.API.exception.ResourceNotFoundException;
import Cloud.ToDo.API.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Page<Task> getTasks(Status status, Priority priority, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        if (status != null && priority != null) {
            return taskRepository.findByStatusAndPriority(status, priority, pageable);
        } else if (status != null) {
            return taskRepository.findByStatus(status, pageable);
        } else if (priority != null) {
            return taskRepository.findByPriority(priority, pageable);
        }
        return taskRepository.findAll(pageable);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    public Task createTask(TaskRequest req) {
        Task task = new Task();
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setStatus(req.getStatus() != null ? req.getStatus() : Status.NEW);
        task.setPriority(req.getPriority() != null ? req.getPriority() : Priority.LOW);
        task.setDueDate(req.getDueDate());
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, TaskRequest req) {
        Task task = getTaskById(id);
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        if (req.getStatus() != null) task.setStatus(req.getStatus());
        if (req.getPriority() != null) task.setPriority(req.getPriority());
        task.setDueDate(req.getDueDate());
        return taskRepository.save(task);
    }

    public Task patchTask(Long id, TaskPatchRequest patch) {
        Task task = getTaskById(id);
        if (patch.getStatus() != null) task.setStatus(patch.getStatus());
        if (patch.getPriority() != null) task.setPriority(patch.getPriority());
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }
}