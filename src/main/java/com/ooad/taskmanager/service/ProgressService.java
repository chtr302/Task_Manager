package com.ooad.taskmanager.service;

import com.ooad.taskmanager.model.Task;
import com.ooad.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class ProgressService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NotificationService notificationService;

    public Task capNhatTrangThai(Long taskId, Task.Status status, String progressNote, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
            new RuntimeException("Task not found with id: " + taskId));

        // Cập nhật trạng thái và thông tin liên quan
        task.setStatus(status);
        task.setProgressNote(progressNote);
        task.setUpdatedBy(userId);

        Task updatedTask = taskRepository.save(task);

        // Gửi thông báo cho trưởng nhóm (có thể dựa trên project leader)
        guiThongBao(taskId);

        return updatedTask;
    }

    public void guiThongBao(Long taskId) {
        // Tìm task để lấy thông tin
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
            new RuntimeException("Task not found with id: " + taskId));

        String message = String.format("Công việc '%s' đã được cập nhật trạng thái thành '%s'",
            task.getName(), formatStatusVietnamese(task.getStatus()));

        notificationService.guiThongBao(taskId, message);
    }

    private String formatStatusVietnamese(Task.Status status) {
        switch(status) {
            case TO_DO: return "Chưa Bắt Đầu";
            case IN_PROGRESS: return "Đang Thực Hiện";
            case REVIEW: return "Đang Xem Xét";
            case DONE: return "Hoàn Thành";
            case CANCELLED: return "Đã Hủy";
            default: return status.toString();
        }
    }

    public List<Task> layDanhSachCongViecTheoNguoiDung(Long userId) {
        // Lấy danh sách task IDs mà user được assign
        String sql = """
            SELECT t.id, t.name, t.description, t.due_date, t.priority, t.status,
                   t.progress_note, t.project_id, t.created_by, t.updated_by,
                   t.created_at, t.updated_at
            FROM Task t
            INNER JOIN Task_Assignees ta ON t.id = ta.task_id
            WHERE ta.employee_id = ?
            ORDER BY t.due_date ASC, t.created_at DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Task task = new Task();
            task.setId(rs.getLong("id"));
            task.setName(rs.getString("name"));
            task.setDescription(rs.getString("description"));

            // Handle date conversion
            java.sql.Date dueDate = rs.getDate("due_date");
            if (dueDate != null) {
                task.setDueDate(dueDate.toLocalDate());
            }

            task.setPriority(rs.getString("priority"));

            // Handle enum status
            String statusStr = rs.getString("status");
            if (statusStr != null) {
                try {
                    task.setStatus(Task.Status.valueOf(statusStr));
                } catch (IllegalArgumentException e) {
                    task.setStatus(Task.Status.TO_DO);
                }
            }

            task.setProgressNote(rs.getString("progress_note"));
            task.setProjectId(rs.getObject("project_id") != null ? rs.getLong("project_id") : null);
            task.setCreatedBy(rs.getObject("created_by") != null ? rs.getLong("created_by") : null);
            task.setUpdatedBy(rs.getObject("updated_by") != null ? rs.getLong("updated_by") : null);

            // Handle timestamps
            java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                task.setCreatedAt(createdAt.toLocalDateTime());
            }

            java.sql.Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                task.setUpdatedAt(updatedAt.toLocalDateTime());
            }

            return task;
        }, userId);
    }
}
