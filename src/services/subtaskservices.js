import { Task } from '../models/task.model.js';



export async function viewSubtaskByTaskService(task_id, statusFilter) {
  const subtasks = await Subtask.findAll({
    where: {
      task_id: task_id,
      ...(statusFilter && { subtask_status: statusFilter }) 
    },
    include: [
      { model: Task, as: 'task', attributes: ['task_id','task_name' ,'task_status'] }
    ]
  });
  return subtasks;
}

export async function changeSubtaskStatusService(subtask_id, newStatus) {
  const subtask = await Subtask.findByPk(subtask_id);
  if (!subtask) throw { status: 404, message: 'Không tìm thấy subtask' };
  subtask.subtask_status = newStatus;
  await subtask.save();
  return subtask;
}





