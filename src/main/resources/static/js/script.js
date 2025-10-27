document.addEventListener('DOMContentLoaded', function() {
    const projectSelect = document.getElementById('projectId');
    const createdBySelect = document.getElementById('createdBy');

    // Fetch and populate projects
    fetch('/api/projects')
        .then(response => response.json())
        .then(projects => {
            const defaultOption = document.createElement('option');
            defaultOption.value = '';
            defaultOption.textContent = '-- None --';
            projectSelect.appendChild(defaultOption);
            projects.forEach(project => {
                const option = document.createElement('option');
                option.value = project.id;
                option.textContent = project.name;
                projectSelect.appendChild(option);
            });
        })
        .catch(error => console.error('Error fetching projects:', error));

    // Fetch and populate employees
    fetch('/api/employees')
        .then(response => response.json())
        .then(employees => {
            const defaultOption = document.createElement('option');
            defaultOption.value = '';
            defaultOption.textContent = '-- None --';
            createdBySelect.appendChild(defaultOption);
            employees.forEach(employee => {
                const option = document.createElement('option');
                option.value = employee.id;
                option.textContent = employee.name;
                createdBySelect.appendChild(option);
            });
        })
        .catch(error => console.error('Error fetching employees:', error));
});

document.getElementById('create-task-form').addEventListener('submit', function(event) {
    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);
    const taskData = Object.fromEntries(formData.entries());

    // Convert empty strings to null for fields that are not required
    if (taskData.description === '') {
        taskData.description = null;
    }
    if (taskData.dueDate === '') {
        taskData.dueDate = null;
    }
    if (taskData.projectId === '') {
        taskData.projectId = null;
    }
    if (taskData.createdBy === '') {
        taskData.createdBy = null;
    }

    const responseMessage = document.getElementById('response-message');

    fetch('/api/tasks', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(taskData),
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => { throw new Error(err.message || 'Something went wrong') });
        }
        return response.json();
    })
    .then(data => {
        responseMessage.style.color = 'green';
        responseMessage.textContent = `Task "${data.name}" created successfully with ID: ${data.id}`;
        form.reset();
    })
    .catch(error => {
        responseMessage.style.color = 'red';
        responseMessage.textContent = `Error: ${error.message}`;
    });
});
