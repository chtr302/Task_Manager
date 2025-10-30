package com.ooad.taskmanager.controller;

import com.ooad.taskmanager.model.Task;
import com.ooad.taskmanager.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> layDanhSachCongViec(@RequestParam Long userId) {
        try {
            List<Task> tasks = progressService.layDanhSachCongViecTheoNguoiDung(userId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<Task> capNhatTrangThai(
            @PathVariable Long taskId,
            @RequestParam Task.Status status,
            @RequestParam(required = false) String progressNote,
            @RequestParam Long userId) {

        try {
            Task updatedTask = progressService.capNhatTrangThai(taskId, status, progressNote, userId);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
