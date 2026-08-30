package Cloud.ToDo.API;

import Cloud.ToDo.API.controller.HealthController;
import Cloud.ToDo.API.controller.TaskController;
import Cloud.ToDo.API.dto.TaskRequest;
import Cloud.ToDo.API.entity.Priority;
import Cloud.ToDo.API.entity.Status;
import Cloud.ToDo.API.entity.Task;
import Cloud.ToDo.API.exception.GlobalExceptionHandler;
import Cloud.ToDo.API.security.ApiKeyAuthFilter;
import Cloud.ToDo.API.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerIntegrationTest {

    private MockMvc mockMvc;
    private MockMvc healthMockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter();
        ReflectionTestUtils.setField(filter, "validApiKey", "dev-secret-key-123");

        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilters(filter)
                .build();

        healthMockMvc = MockMvcBuilders.standaloneSetup(new HealthController())
                .addFilters(filter)
                .build();
    }

    @Test
    void testHealthEndpoint_PublicAccess() throws Exception {
        healthMockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void testUnauthorizedWithoutApiKey() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void testCreateTask_ValidationError() throws Exception {
        TaskRequest invalidRequest = new TaskRequest();

        mockMvc.perform(post("/api/tasks")
                        .header("X-API-Key", "dev-secret-key-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void testCreateTask_Success() throws Exception {
        TaskRequest req = new TaskRequest();
        req.setTitle("Valid Task Title");
        req.setPriority(Priority.HIGH);
        req.setStatus(Status.NEW);

        Task created = new Task();
        created.setId(1L);
        created.setTitle("Valid Task Title");

        when(taskService.createTask(any(TaskRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/tasks")
                        .header("X-API-Key", "dev-secret-key-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Valid Task Title"));
    }
}