package com.ooad.taskmanager.controller;

import com.ooad.taskmanager.model.Task;
import com.ooad.taskmanager.service.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskManager taskManager;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody Task task) {
        return taskManager.createTask(task);
    }
}
