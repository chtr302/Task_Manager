package com.ooad.taskmanager.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ooad.taskmanager.model.Task;
import com.ooad.taskmanager.model.TaskAssignee;
import com.ooad.taskmanager.repository.TaskAssigneeRepository;
import com.ooad.taskmanager.repository.TaskRepository;


@Service
public class TaskManager {

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Autowired
    private TaskAssigneeRepository taskAssigneeRepository;

    public boolean giaoViecChoThanhVien(TaskAssignee assignee) {
        if (taskAssigneeRepository.existsByTaskIdAndEmployeeId(assignee.getTaskId(), assignee.getEmployeeId()))
            return false;
        taskAssigneeRepository.save(assignee);
        return true;
    }

}
