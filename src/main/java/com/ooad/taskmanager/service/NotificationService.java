package com.ooad.taskmanager.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void guiThongBao(Long taskId, String message) {
        // TODO: Implement actual notification logic
        // Có thể gửi email, push notification, hoặc lưu vào bảng notifications

        // Hiện tại chỉ log thông báo
        System.out.println("NOTIFICATION: Task ID " + taskId + " - " + message);

        // Trong tương lai có thể:
        // - Gửi email cho project leader
        // - Gửi push notification
        // - Lưu vào bảng notifications trong database
    }
}
