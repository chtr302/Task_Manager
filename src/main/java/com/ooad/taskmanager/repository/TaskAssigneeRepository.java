package com.ooad.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ooad.taskmanager.model.TaskAssignee;
import com.ooad.taskmanager.model.TaskAssigneeId;

public interface TaskAssigneeRepository extends JpaRepository<TaskAssignee, TaskAssigneeId> {
    boolean existsByTaskIdAndEmployeeId(Long taskId, Long employeeId);
}
