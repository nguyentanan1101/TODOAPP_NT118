import pool from '../config/database.js';

export class Subtask {
  constructor(id, subtask_name, description, status, task_id, createdAt, due_date) {
    this.id = id;
    this.subtask_name = subtask_name;
    this.description = description;
    this.status = status;
    this.task_id = task_id;
    this.createdAt = createdAt;
    this.due_date = due_date;
  } 
}

export async function createSubtask(subtask_name, description, status, task_id, due_date) {
    const [result] = await pool.query(
        'INSERT INTO subtask (subtask_name, description, status, task_id, due_date) VALUES (?, ?, ?, ?, ?)',   
        [subtask_name, description, status, task_id, due_date]
    );
    return new Subtask(result.insertId, subtask_name, description, status, task_id, new Date(), due_date);
}   