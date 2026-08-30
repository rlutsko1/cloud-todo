package Cloud.ToDo.API.repository;

import Cloud.ToDo.API.entity.Priority;
import Cloud.ToDo.API.entity.Status;
import Cloud.ToDo.API.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByStatus(Status status, Pageable pageable);

    Page<Task> findByPriority(Priority priority, Pageable pageable);

    Page<Task> findByStatusAndPriority(
            Status status,
            Priority priority,
            Pageable pageable
    );
}