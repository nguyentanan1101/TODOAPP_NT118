import { createTask, updateTask, deleteTask,findTaskIdByName, getAllTasks } from "../models/task.model.js";


export async function CreateTask(req, res, next) {
    const { task_name, description, task_priority, task_progress = 0, status, Due_date } = req.body;
    
    try {
        const newTask = await createTask(task_name, description, task_priority, task_progress, status, Due_date);
        res.status(201).json(newTask);
        console.log('Task created:', newTask);
    } catch (error) {
        next(error);
    }
}

export async function GetAllTasks(req, res, next) {
    try {
        const tasks = await getAllTasks();
        res.status(200).json(tasks);
    } catch (error) {
        next(error);
    }
}

export async function UpdateTask(req, res, next) {
  try {
    const fieldsToUpdate = req.body;
    let taskId = await findTaskIdByName(fieldsToUpdate.task_name);
    if (!taskId) {
      return res.status(404).json({ error: 'Task not found' });
    }
    const updatedTask = await updateTask(taskId, fieldsToUpdate);
    res.status(200).json(updatedTask);
    console.log('Task updated:', updatedTask);
  } catch (error) {
    next(error);
  }
}

export async function DeleteTask(req, res, next) {
  try {
    const { id } = req.params;
    await deleteTask(id);
    res.status(200).json({ message: 'Task deleted successfully' });
    console.log('Task deleted with ID:', id);
  } catch (error) {
    next(error);
  }
}
