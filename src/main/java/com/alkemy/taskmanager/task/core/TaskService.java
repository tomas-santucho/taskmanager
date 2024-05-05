package com.alkemy.taskmanager.task.core;


import com.alkemy.taskmanager.task.data.TaskRepository;
import com.alkemy.taskmanager.task.domain.Task;
import com.alkemy.taskmanager.task.domain.TaskStatus;
import com.alkemy.taskmanager.task.dto.TaskDTO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskService {


    private final TaskRepository taskRepository;


    private ModelMapper modelMapper;

    public TaskDTO createTask(TaskDTO taskDTO) {
        var task = modelMapper.map(taskDTO, Task.class);
        task.setStatus(TaskStatus.valueOf(taskDTO.status()));
        task = taskRepository.save(task);
        return modelMapper.map(task, TaskDTO.class);
    }

    public Page<TaskDTO> getAllTasks(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var tasksPage = taskRepository.findAll(pageable);
        return tasksPage.map(task -> modelMapper.map(task, TaskDTO.class));
    }

    public TaskDTO getTaskById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id));
        return modelMapper.map(task, TaskDTO.class);
    }

    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id));
        task.setDescription(taskDTO.description());
        task.setStatus(TaskStatus.valueOf(taskDTO.status()));
        task = taskRepository.save(task);
        return modelMapper.map(task, TaskDTO.class);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}