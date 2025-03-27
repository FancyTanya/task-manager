package org.bitbucket.privattaskmanager.service;

import org.bitbucket.privattaskmanager.domain.Task;

import java.util.List;

public interface TaskService {

    Task createTask(Task task);

    boolean updateTask(Long taskId, Task task);

    boolean deleteTask(Long taskId);

    List<Task> findAll();

    boolean updateTaskStatus(Long taskId, String status);
}
