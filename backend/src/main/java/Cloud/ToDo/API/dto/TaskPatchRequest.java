package Cloud.ToDo.API.dto;

import Cloud.ToDo.API.entity.Priority;
import Cloud.ToDo.API.entity.Status;

public class TaskPatchRequest {
    private Status status;
    private Priority priority;

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}