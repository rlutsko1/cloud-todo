package Cloud.ToDo.API;

import Cloud.ToDo.API.dto.TaskPatchRequest;
import Cloud.ToDo.API.dto.TaskRequest;
import Cloud.ToDo.API.entity.Priority;
import Cloud.ToDo.API.entity.Status;
import Cloud.ToDo.API.entity.Task;
import Cloud.ToDo.API.exception.ResourceNotFoundException;
import Cloud.ToDo.API.repository.TaskRepository;
import Cloud.ToDo.API.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setStatus(Status.NEW);
        testTask.setPriority(Priority.LOW);
    }

    @Test
    void testGetTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        Task result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(99L));
    }

    @Test
    void testCreateTask_Success() {
        TaskRequest req = new TaskRequest();
        req.setTitle("New Task");
        req.setStatus(Status.NEW);
        req.setPriority(Priority.HIGH);

        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task created = taskService.createTask(req);

        assertNotNull(created);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskRequest req = new TaskRequest();
        req.setTitle("Updated Title");

        Task updated = taskService.updateTask(1L, req);

        assertNotNull(updated);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testPatchTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskPatchRequest patch = new TaskPatchRequest();
        patch.setStatus(Status.DONE);

        Task patched = taskService.patchTask(1L, patch);

        assertNotNull(patched);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testDeleteTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doNothing().when(taskRepository).delete(testTask);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(testTask);
    }

    @Test
    void testGetAllTasks_Success() {
        Page<Task> page = new PageImpl<>(List.of(testTask));
        when(taskRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Task> result = taskService.getTasks(null, null, 0, 10);

        assertEquals(1, result.getTotalElements());
        verify(taskRepository, times(1)).findAll(any(Pageable.class));
    }
}