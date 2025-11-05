import { DataTypes } from 'sequelize';
import sequelize from '../config/db.js';

export const Task = sequelize.define('Task', {
  ID: { 
    type: DataTypes.INTEGER, 
    autoIncrement: true, 
    primaryKey: true 
  },
  Task_name: { 
    type: DataTypes.STRING(100), 
    allowNull: false, 
    unique: true 
  },
  Description: { type: DataTypes.TEXT },
  Task_priority: { 
    type: DataTypes.ENUM('low', 'medium', 'high'),
    defaultValue: 'medium'
  },
  Task_progress: { 
    type: DataTypes.INTEGER,
    defaultValue: 0
  },
  Status: { 
    type: DataTypes.ENUM('todo', 'in_progress', 'done'),
    defaultValue: 'todo'
  },
  Create_date: { 
    type: DataTypes.DATE, 
    defaultValue: DataTypes.NOW 
  },
  Due_date: { type: DataTypes.DATE },
  Update_date: { type: DataTypes.DATE },
  Project_ID: { type: DataTypes.INTEGER, allowNull: false }
}, {
  tableName: 'task',
  timestamps: false
});



export async function createTask(task_name, description, task_priority, task_progress, status, Due_date) {
  const task = await Task.create({ task_name, description, task_priority, task_progress, status, Due_date });
  return task;
}


export async function findTaskIdByName(task_name) {
  const task = await Task.findOne({ where: { task_name } });
  return task?.id;
}


export async function updateTask(id, fields) {
  await Task.update(fields, { where: { id } });
  return { id, ...fields };
}


export async function deleteTask(id) {
  await Task.destroy({ where: { id } });
}


export async function getAllTasks() {
  return await Task.findAll();
}


export async function getTaskByName(task_name) {
  return await Task.findOne({ where: { task_name } });
}

