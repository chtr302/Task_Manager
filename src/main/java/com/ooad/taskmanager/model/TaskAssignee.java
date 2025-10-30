package com.ooad.taskmanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Task_Assignees")
@IdClass(TaskAssigneeId.class)
public class TaskAssignee {

    @Id
    @Column(name = "task_id")
    private Long taskId;

    @Id
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "role_in_task")
    private String roleInTask;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    public TaskAssignee() {}

    public TaskAssignee(Long taskId, Long employeeId, String roleInTask) {
        this.taskId = taskId;
        this.employeeId = employeeId;
        this.roleInTask = roleInTask;
        this.assignedAt = LocalDateTime.now();
    }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getRoleInTask() { return roleInTask; }
    public void setRoleInTask(String roleInTask) { this.roleInTask = roleInTask; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
}