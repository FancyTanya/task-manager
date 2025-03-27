package org.bitbucket.privattaskmanager.repository;

import org.bitbucket.privattaskmanager.domain.Task;
import org.bitbucket.privattaskmanager.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByTitle(String title);

    @NativeQuery(value = "SELECT count(t.id) FROM task t WHERE t.task_status IN (?, ?); ")
    int countByTaskStatusAndTaskStatus(TaskStatus taskStatus, TaskStatus taskStatus1);
}
