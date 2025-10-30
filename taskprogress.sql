-- 1. Tạo Database (nếu chưa tồn tại)
CREATE DATABASE IF NOT EXISTS TaskManager_Progress;

-- 2. Sử dụng Database TaskManager_Progress
USE TaskManager_Progress;

-- 3. Tạo bảng Employee

CREATE TABLE IF NOT EXISTS Employee (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    position VARCHAR(255)
);

-- 4. Tạo bảng Project
CREATE TABLE IF NOT EXISTS Project (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50)
);

-- 5. Tạo bảng Task (ĐÃ CẬP NHẬT THEO DÕI TIẾN ĐỘ)
CREATE TABLE IF NOT EXISTS Task (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE,
    priority VARCHAR(20),
    status ENUM('To Do', 'In Progress', 'Review', 'Done', 'Cancelled') DEFAULT 'To Do',
    progress_note TEXT,  -- <== Ghi chú tiến độ (thành viên nhập)
    project_id INT,
    created_by INT,
    updated_by INT,      -- <== Người cập nhật tiến độ gần nhất
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES Project(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (created_by) REFERENCES Employee(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES Employee(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- 6. Tạo bảng Project_Members
CREATE TABLE IF NOT EXISTS Project_Members (
    project_id INT,
    employee_id INT,
    role_in_project VARCHAR(255),
    PRIMARY KEY (project_id, employee_id),
    FOREIGN KEY (project_id) REFERENCES Project(id)
        ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES Employee(id)
        ON DELETE CASCADE
);

-- 7. Tạo bảng Task_Assignees
CREATE TABLE IF NOT EXISTS Task_Assignees (
    task_id INT,
    employee_id INT,
    role_in_task VARCHAR(255),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (task_id, employee_id),
    FOREIGN KEY (task_id) REFERENCES Task(id)
        ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES Employee(id)
        ON DELETE CASCADE
);

