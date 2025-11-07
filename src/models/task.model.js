import { DataTypes, Sequelize } from 'sequelize';
import sequelize from '../config/db.js';

export const Task = sequelize.define('Task', {
  task_id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  task_name: {
    type: DataTypes.STRING(100),
    allowNull: false
  },
  task_description: {
    type: DataTypes.TEXT,
    allowNull: true
  },
  task_status: {
    type: DataTypes.ENUM('ToDo', 'Working', 'Done'),
    allowNull: false,
    defaultValue: 'ToDo'
  },
  task_progress: {
    type: DataTypes.INTEGER,
    allowNull: false,
    defaultValue: 0
  },
  created_date: {
    type: DataTypes.DATE,
    allowNull: false,
    defaultValue: DataTypes.NOW
  },
  start_date: {
    type: DataTypes.DATE,
    allowNull: true
  },
  due_date: {
    type: DataTypes.DATE,
    allowNull: true
  },
  project_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'project',
      key: 'project_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  },

  created_by: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'user',
      key: 'user_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  },

  assigned_to: {
    type: DataTypes.INTEGER,
    allowNull: true,
    references: {
      model: 'user',
      key: 'user_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'SET NULL'
  }

}, {
  tableName: 'task',
  timestamps: false,
  indexes: [
    {
      name: 'unique_task_per_project',
      unique: true,
      fields: ['task_name', 'project_id']
    }
  ]
});

export default Task;



