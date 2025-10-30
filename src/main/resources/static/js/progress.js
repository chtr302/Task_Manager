document.addEventListener('DOMContentLoaded', function() {
    const loadTasksBtn = document.getElementById('load-tasks-btn');
    const tasksList = document.getElementById('tasks-list');
    const updateForm = document.getElementById('update-form');
    const progressUpdateForm = document.getElementById('progress-update-form');
    const cancelUpdateBtn = document.getElementById('cancel-update');
    const responseMessage = document.getElementById('response-message');

    // Load tasks for user
    loadTasksBtn.addEventListener('click', function() {
        const userId = document.getElementById('userId').value;
        if (!userId) {
            showMessage('Please enter a valid User ID', 'error');
            return;
        }

        fetch(`/api/progress/tasks?userId=${userId}`)
            .then(response => response.json())
            .then(tasks => {
                displayTasks(tasks);
            })
            .catch(error => {
                console.error('Error loading tasks:', error);
                showMessage('Error loading tasks', 'error');
            });
    });

    // Handle task update form submission
    progressUpdateForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const taskId = document.getElementById('update-task-id').value;
        const status = document.getElementById('new-status').value;
        const progressNote = document.getElementById('progress-note').value;
        const userId = document.getElementById('userId').value;

        fetch(`/api/progress/tasks/${taskId}/status?status=${status}&progressNote=${encodeURIComponent(progressNote)}&userId=${userId}`, {
            method: 'PUT'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update task');
            }
            return response.json();
        })
        .then(updatedTask => {
            showMessage(`Task "${updatedTask.name}" updated successfully!`, 'success');
            updateForm.style.display = 'none';
            // Reload tasks
            loadTasksBtn.click();
        })
        .catch(error => {
            console.error('Error updating task:', error);
            showMessage('Error updating task', 'error');
        });
    });

    // Cancel update
    cancelUpdateBtn.addEventListener('click', function() {
        updateForm.style.display = 'none';
    });

    function displayTasks(tasks) {
        if (tasks.length === 0) {
            tasksList.innerHTML = '<p>No tasks found for this user.</p>';
            return;
        }

        let html = '<div class="tasks-grid">';
        tasks.forEach(task => {
            const statusClass = getStatusClass(task.status);
            html += `
                <div class="task-card ${statusClass}">
                    <h3>${task.name}</h3>
                    <p><strong>Description:</strong> ${task.description || 'N/A'}</p>
                    <p><strong>Status:</strong> <span class="status-badge ${statusClass}">${formatStatus(task.status)}</span></p>
                    <p><strong>Due Date:</strong> ${task.dueDate || 'N/A'}</p>
                    <p><strong>Progress Note:</strong> ${task.progressNote || 'No progress note'}</p>
                    <p><strong>Last Updated:</strong> ${task.updatedAt || task.createdAt || 'N/A'}</p>
                    <button class="update-btn" data-task-id="${task.id}" data-task='${JSON.stringify(task).replace(/'/g, "&apos;")}'>Update Progress</button>
                </div>
            `;
        });
        html += '</div>';
        tasksList.innerHTML = html;

        // Add event listeners to update buttons
        document.querySelectorAll('.update-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const task = JSON.parse(this.getAttribute('data-task'));
                showUpdateForm(task);
            });
        });
    }

    function showUpdateForm(task) {
        document.getElementById('update-task-id').value = task.id;
        document.getElementById('task-name').value = task.name;
        document.getElementById('current-status').value = formatStatus(task.status);
        document.getElementById('new-status').value = task.status;
        document.getElementById('progress-note').value = task.progressNote || '';

        updateForm.style.display = 'block';
        updateForm.scrollIntoView({ behavior: 'smooth' });
    }

    function getStatusClass(status) {
        switch(status) {
            case 'TO_DO': return 'status-todo';
            case 'IN_PROGRESS': return 'status-inprogress';
            case 'REVIEW': return 'status-review';
            case 'DONE': return 'status-done';
            case 'CANCELLED': return 'status-cancelled';
            default: return 'status-todo';
        }
    }

    function formatStatus(status) {
        switch(status) {
            case 'TO_DO': return 'To Do';
            case 'IN_PROGRESS': return 'In Progress';
            case 'REVIEW': return 'Review';
            case 'DONE': return 'Done';
            case 'CANCELLED': return 'Cancelled';
            default: return status;
        }
    }

    function showMessage(message, type) {
        responseMessage.textContent = message;
        responseMessage.className = type;
        responseMessage.style.display = 'block';

        setTimeout(() => {
            responseMessage.style.display = 'none';
        }, 5000);
    }
});
