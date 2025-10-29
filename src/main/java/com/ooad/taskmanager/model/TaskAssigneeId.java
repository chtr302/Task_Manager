package com.ooad.taskmanager.model;
import java.io.Serializable;
import java.util.Objects;

public class TaskAssigneeId implements Serializable {
    private Long taskId;
    private Long employeeId;

    public TaskAssigneeId() {}
    public TaskAssigneeId(Long taskId, Long employeeId) {
        this.taskId = taskId;
        this.employeeId = employeeId;
    }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskAssigneeId that = (TaskAssigneeId) o;
        return Objects.equals(taskId, that.taskId) && Objects.equals(employeeId, that.employeeId);
    }
    @Override
    public int hashCode() { return Objects.hash(taskId, employeeId); }
}
