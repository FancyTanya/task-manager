package org.bitbucket.privattaskmanager.service;

import org.bitbucket.privattaskmanager.domain.Task;
import org.bitbucket.privattaskmanager.domain.TaskStatus;
import org.bitbucket.privattaskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task.");
        task.setTaskStatus(TaskStatus.NEW);
    }

    @Test
    public void testCreateTask_Success() {
        when(taskRepository.countByTaskStatusAndTaskStatus(TaskStatus.IN_PROGRESS, TaskStatus.NEW)).thenReturn(0);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskRepository.existsByTitle(task.getTitle())).thenReturn(false);

        Task createdTask = taskService.createTask(task);

        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getTitle());
        verify(taskRepository).save(task);
    }

    @Test
    public void testCreateTask_TaskLimitExceeded() {
        when(taskRepository.countByTaskStatusAndTaskStatus(TaskStatus.IN_PROGRESS, TaskStatus.NEW)).thenReturn(11);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.createTask(task);
        });

        assertEquals("Amount active tasks exceeds new task amount", exception.getMessage());
    }

    @Test
    public void testFindAll() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.findAll();

        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
    }

    @Test
    public void testUpdateTaskStatus_TaskExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        boolean updated = taskService.updateTaskStatus(1L, "IN PROGRESS");

        assertTrue(updated);
        assertEquals(TaskStatus.IN_PROGRESS, task.getTaskStatus());
        verify(taskRepository).save(task);
    }

    @Test
    public void testUpdateTaskStatus_TaskDoesNotExist() {
        when(taskRepository.findById(2L)).thenReturn(Optional.empty());

        boolean updated = taskService.updateTaskStatus(2L, "IN PROGRESS");

        assertFalse(updated);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void testUpdateTask_TitleOnly() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        Task newTask = new Task();
        newTask.setTitle("Updated Task");
        newTask.setDescription("Updated description.");

        when(taskRepository.existsByTitle(newTask.getTitle())).thenReturn(false);

        boolean isUpdated = taskService.updateTask(1L, newTask);

        assertTrue(isUpdated);
        assertEquals("Updated Task", task.getTitle());
        verify(taskRepository).save(task);
    }

    @Test
    public void testDeleteTask_TaskExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        boolean isDeleted = taskService.deleteTask(1L);

        assertTrue(isDeleted);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    public void testDeleteTask_TaskDoesNotExist() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        boolean isDeleted = taskService.deleteTask(1L);

        assertFalse(isDeleted);
        verify(taskRepository, never()).deleteById(1L);
    }
}