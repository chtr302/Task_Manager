-- 1. Tạo Database (nếu chưa tồn tại)
CREATE DATABASE IF NOT EXISTS TaskManager;

-- 2. Sử dụng Database TaskManager
USE TaskManager;

-- 3. Tạo bảng Employee
CREATE TABLE Employee (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    position VARCHAR(255)
);

-- 4. Tạo bảng Project
CREATE TABLE Project (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50)
);

-- 5. Tạo bảng Task
CREATE TABLE Task (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE,
    priority VARCHAR(20),
    status ENUM('To Do', 'In Progress', 'Review', 'Done', 'Cancelled'),
    project_id INT,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES Project(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (created_by) REFERENCES Employee(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- 6. Tạo bảng Project_Members
CREATE TABLE Project_Members (
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
CREATE TABLE Task_Assignees (
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
