package com.ooad.taskmanager.controller;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ooad.taskmanager.model.Task;
import com.ooad.taskmanager.model.TaskAssignee;
import com.ooad.taskmanager.service.TaskManager;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private com.ooad.taskmanager.repository.EmployeeRepository employeeRepository;
    @Autowired
    private com.ooad.taskmanager.repository.ProjectRepository projectRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody Task task) {
        return taskManager.createTask(task);
    }

    @GetMapping
    public List<Map<String, Object>> getAllTasks() {
        String sql = "SELECT id, name, due_date, project_id FROM Task";
        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", rs.getLong("id"));
            m.put("name", rs.getString("name"));
            Date d = rs.getDate("due_date");
            m.put("dueDate", d == null ? null : d.toLocalDate().toString());
            Object pid = rs.getObject("project_id");
            m.put("projectId", pid == null ? null : rs.getLong("project_id"));
            return m;
        });
    }

    @GetMapping("/employees")
    public List<com.ooad.taskmanager.model.Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @GetMapping("/projects")
    public List<com.ooad.taskmanager.model.Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @GetMapping("/roles")
    public List<String> getAllRoles() {
        String sql = "SELECT DISTINCT role_in_project FROM Project_Members WHERE role_in_project IS NOT NULL";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public static record EmployeeAssign(Long employeeId, String roleInTask) {}
    public static record AssignRequest(Long taskId, List<EmployeeAssign> assignees) {}

    @PostMapping("/assign")
    public ResponseEntity<String> assign(@RequestBody AssignRequest req) {
        if (req == null || req.taskId() == null || req.assignees() == null || req.assignees().isEmpty()) {
            return ResponseEntity.badRequest().body("Yêu cầu không hợp lệ: phải có taskId và ít nhất một assignee.");
        }

        StringBuilder successMsg = new StringBuilder();
        StringBuilder failMsg = new StringBuilder();
        int success = 0, skipped = 0;

        for (EmployeeAssign ea : req.assignees()) {
            if (ea == null || ea.employeeId() == null) {
                skipped++;
                failMsg.append("- Không xác định nhân viên\n");
                continue;
            }
            TaskAssignee ta = new TaskAssignee(req.taskId(), ea.employeeId(), ea.roleInTask());
            boolean ok = taskManager.giaoViecChoThanhVien(ta);
            String empName = getEmployeeName(ea.employeeId());
            if (ok) {
                success++;
                successMsg.append(String.format("- %s (ID:%d)\n", empName, ea.employeeId()));
            } else {
                skipped++;
                failMsg.append(String.format("- %s (ID:%d): Đã được giao trước đó\n", empName, ea.employeeId()));
            }
        }

        StringBuilder msg = new StringBuilder();
        msg.append(String.format("Giao việc thành công cho %d nhân viên:\n", success));
        msg.append(successMsg.length() > 0 ? successMsg : "- Không có\n");
        if (skipped > 0) {
            msg.append(String.format("Không giao được cho %d nhân viên:\n", skipped));
            msg.append(failMsg.length() > 0 ? failMsg : "- Không có\n");
        }
        return ResponseEntity.ok(msg.toString());
    }

    private String getEmployeeName(Long employeeId) {
        try {
            var emp = employeeRepository.findById(employeeId);
            return emp.isPresent() ? emp.get().getName() : "";
        } catch (Exception ex) {
            return "";
        }
    }
}
