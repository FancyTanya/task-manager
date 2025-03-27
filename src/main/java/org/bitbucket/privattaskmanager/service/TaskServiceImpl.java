package org.bitbucket.privattaskmanager.service;

import org.bitbucket.privattaskmanager.datasource.DatabaseOperationHandler;
import org.bitbucket.privattaskmanager.domain.Task;
import org.bitbucket.privattaskmanager.domain.TaskStatus;
import org.bitbucket.privattaskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private static final int TASK_AMOUNT_IN_PROGRESS_OR_NEW = 10;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        int amountActiveTasks = DatabaseOperationHandler.execute(() ->
                taskRepository.countByTaskStatusAndTaskStatus(TaskStatus.IN_PROGRESS, TaskStatus.NEW));

        if (amountActiveTasks >= TASK_AMOUNT_IN_PROGRESS_OR_NEW) {
            logger.error("Amount active tasks exceeds new task amount");
            return null;
        }

        if (titleValidation(task)) {
            DatabaseOperationHandler.execute(() -> {
                taskRepository.save(task);
                logger.info("Created new task: {}", task.getTitle());
                return null;
            });
        }
        return task;
    }

    public List<Task> findAll() {
        return DatabaseOperationHandler.execute(taskRepository::findAll);
    }

    public boolean updateTaskStatus(Long taskId, String status) {
        TaskStatus taskStatus = TaskStatus.fromValue(status);
        Optional<Task> optionalTask = DatabaseOperationHandler.execute(() -> taskRepository.findById(taskId));
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setTaskStatus(taskStatus);
            DatabaseOperationHandler.execute(() -> {
                taskRepository.save(task);
                logger.info("Updated task: {}", task.getTitle());
                return null;
            });
            return true;
        }
        return false;
    }

    public boolean updateTask(Long taskId, Task task) {
        Task existsTask = DatabaseOperationHandler.execute(() -> taskRepository.findById(taskId).orElse(null));
        boolean isUpdated = false;

        if (existsTask != null) {
            if (!Objects.equals(existsTask.getTitle(), task.getTitle())) {
                if (titleValidation(task)) {
                    logger.info("Changed Title task from: {}, to: {}", existsTask.getTitle(), task.getTitle());
                    existsTask.setTitle(task.getTitle());
                    isUpdated = true;
                }
            }
            if (!Objects.equals(existsTask.getDescription(), task.getDescription())) {
                logger.info("Changed Description from: {}, to: {}", existsTask.getDescription(), task.getDescription());
                existsTask.setDescription(task.getDescription());
                isUpdated = true;
            }
            if (isUpdated) {
                DatabaseOperationHandler.execute(() -> {
                    taskRepository.save(existsTask);
                    logger.info("Updated task: {}", existsTask.getTitle());
                    return null;
                });
            }
        }
        return isUpdated;
    }

    public boolean deleteTask(Long taskId) {
        boolean isDeleted = false;
        if (DatabaseOperationHandler.execute(() -> taskRepository.existsById(taskId))) {
            DatabaseOperationHandler.execute(() -> {
                taskRepository.deleteById(taskId);
                logger.info("Deleted task: {}", taskId);
                return null;
            });
            isDeleted = true;
        }
        return isDeleted;
    }

    private boolean titleValidation(Task task) {
        return !DatabaseOperationHandler.execute(() -> taskRepository.existsByTitle(task.getTitle()));
    }

}
