import { Task } from '../models/task.model.js';
import { User } from '../models/auth.model.js';



export async function viewTasksByUserService(user_id, statusFilter) {
  const tasks = await Task.findAll({
    where: {
      assigned_to: user_id,
      ...(statusFilter && { task_status: statusFilter }) // chỉ thêm vào nếu có
    },
    include: [
      { model: User, as: 'creator', attributes: ['user_id', 'email'] },
      { model: User, as: 'assignee', attributes: ['user_id', 'email'] }
    ]
  });

  return tasks;
}


export async function changeTaskStatusService(task_id, newStatus) {
  const task = await Task.findByPk(task_id);
  if (!task) throw { status: 404, message: 'Task not found' };
  task.task_status = newStatus;
  await task.save();
  return task;
}


