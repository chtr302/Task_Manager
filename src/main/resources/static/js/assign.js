document.addEventListener('DOMContentLoaded', () => {
	const projectSel = document.getElementById('assignProjectId');
	const taskSel = document.getElementById('assignTaskId');
	const dueDateInput = document.getElementById('taskDueDate');
	const pickerEl = document.getElementById('employee-picker');
	const openPickerBtn = document.getElementById('open-employee-picker');
	const resultEl = document.getElementById('assign-result');
	const form = document.getElementById('assign-task-form');

	let allTasks = [];
	let allEmployees = [];
	let allRoles = [];

	const safeName = e => e?.name || e?.fullName || e?.username || `ID:${e?.id || ''}`;

	function fillSelect(selectEl, items, valueKey = 'id', textKey = 'name', defaultLabel = '-- Chọn --') {
		selectEl.innerHTML = '';
		const def = document.createElement('option'); def.value = ''; def.textContent = defaultLabel; selectEl.appendChild(def);
		(items || []).forEach(it => {
			const o = document.createElement('option');
			o.value = it[valueKey];
			o.textContent = it[textKey] ?? (typeof it === 'string' ? it : String(it[valueKey]));
			selectEl.appendChild(o);
		});
	}

	function populateTasks(tasks) {
		fillSelect(taskSel, tasks, 'id', 'name', '-- Chọn công việc --');
		dueDateInput.value = '';
	}

	function renderEmployeePicker(employees, roles) {
		if (!pickerEl || !employees) return;
		pickerEl.innerHTML = '';
		(employees || []).forEach(emp => {
			const row = document.createElement('div');
			row.className = 'emp-row';

			// checkbox with unique id
			const cb = document.createElement('input');
			cb.type = 'checkbox';
			cb.id = `emp-checkbox-${emp.id}`;
			cb.setAttribute('data-emp-id', String(emp.id));

			// label wraps checkbox + name so name sits right beside the checkbox
			const label = document.createElement('label');
			label.className = 'emp-label';
			label.htmlFor = cb.id;

			const name = document.createElement('span');
			name.className = 'emp-name';
			name.textContent = safeName(emp);

			label.appendChild(cb);
			label.appendChild(name);

			const roleSelect = document.createElement('select');
			roleSelect.className = 'emp-role';
			roleSelect.setAttribute('data-emp-id', String(emp.id));
			const emptyOpt = document.createElement('option');
			emptyOpt.value = '';
			emptyOpt.textContent = '-- Chọn vai trò --';
			roleSelect.appendChild(emptyOpt);
			(roles || []).forEach(r => {
				const o = document.createElement('option');
				o.value = r;
				o.textContent = r;
				roleSelect.appendChild(o);
			});

			row.appendChild(label);
			row.appendChild(roleSelect);
			pickerEl.appendChild(row);
		});
	}

	function resetForm() {
		if (!form) return;
		form.reset();
		if (dueDateInput) dueDateInput.value = '';
		if (pickerEl) {
			pickerEl.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.checked = false);
			pickerEl.querySelectorAll('select').forEach(s => s.selectedIndex = 0);
			pickerEl.style.display = 'none';
		}
		if (projectSel) projectSel.selectedIndex = 0;
		if (taskSel) taskSel.selectedIndex = 0;
	}

	openPickerBtn.addEventListener('click', () => {
		if (!pickerEl) return;
		pickerEl.style.display = pickerEl.style.display === 'none' ? 'block' : 'none';
	});

	taskSel.addEventListener('change', e => {
		const taskId = Number(e.target.value);
		const t = allTasks.find(tt => tt.id === taskId);
		dueDateInput.value = t && t.dueDate ? t.dueDate : '';
	});

	projectSel.addEventListener('change', e => {
		const projectId = e.target.value ? Number(e.target.value) : null;
		const filtered = projectId ? allTasks.filter(t => t.projectId === projectId) : allTasks;
		populateTasks(filtered);
	});

	form.addEventListener('submit', e => {
		e.preventDefault();
		const taskId = Number(form.taskId.value);
		if (!taskId) { resultEl.textContent = 'Vui lòng chọn công việc.'; return; }

		const checked = Array.from(pickerEl.querySelectorAll('input[type="checkbox"]:checked'));
		const assignees = checked.map(cb => {
			const empId = Number(cb.getAttribute('data-emp-id'));
			const roleSelect = pickerEl.querySelector(`select[data-emp-id="${empId}"]`);
			const role = roleSelect ? roleSelect.value : null;
			return { employeeId: empId, roleInTask: role };
		}).filter(a => a.employeeId);

		if (assignees.length === 0) { resultEl.textContent = 'Vui lòng chọn ít nhất một nhân viên.'; return; }

		fetch('/api/tasks/assign', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ taskId, assignees })
		})
		.then(r => r.text())
		.then(msg => {
			resultEl.textContent = msg;
			const m = msg ? msg.match(/thành công=(\d+)/) : null;
			if (m && parseInt(m[1], 10) > 0) resetForm();
		})
		.catch(err => resultEl.textContent = `Error: ${err.message}`);
	});

	Promise.all([
		fetch('/api/tasks/projects').then(r => r.ok ? r.json() : []),
		fetch('/api/tasks').then(r => r.ok ? r.json() : []),
		fetch('/api/tasks/employees').then(r => r.ok ? r.json() : []),
		fetch('/api/tasks/roles').then(r => r.ok ? r.json() : [])
	]).then(([projects, tasks, employees, roles]) => {
		allTasks = tasks || [];
		allEmployees = employees || [];
		allRoles = roles || [];

		if (projectSel) fillSelect(projectSel, projects, 'id', 'name', '-- Chọn dự án --');
		populateTasks(allTasks);
		renderEmployeePicker(allEmployees, allRoles);

	}).catch(() => {

	});
});
