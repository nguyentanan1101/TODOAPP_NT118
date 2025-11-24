import { DataTypes } from 'sequelize';
import sequelize from '../config/db.js';

export const Task = sequelize.define('Task', {
  task_id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  project_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'projects',
      key: 'project_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  },
  milestone_id: {
    type: DataTypes.INTEGER,
    allowNull: true,
    references: {
      model: 'milestones',
      key: 'milestone_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'SET NULL'
  },
  title: {
    type: DataTypes.STRING(255),
    allowNull: false
  },
  description: {
    type: DataTypes.TEXT,
    allowNull: true
  },
  status: {
    type: DataTypes.ENUM('To Do', 'In Progress', 'Review', 'Done', 'Blocked'),
    allowNull: false,
    defaultValue: 'To Do'
  },
  task_progress: {
    type: DataTypes.DECIMAL(5, 2),
    allowNull: false,
    defaultValue: 0.00
  },
  created_by: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'users',
      key: 'user_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'RESTRICT'
  },
  assigned_to: {
    type: DataTypes.INTEGER,
    allowNull: true,
    references: {
      model: 'users',
      key: 'user_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'SET NULL'
  },
  step_id: {
    type: DataTypes.INTEGER,
    allowNull: true,
    references: {
      model: 'workflow_steps',
      key: 'step_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'SET NULL'
  },
  created_at: {
    type: DataTypes.DATE,
    defaultValue: DataTypes.NOW
  },
  updated_at: {
    type: DataTypes.DATE,
    defaultValue: DataTypes.NOW
  },
  start_date: {
    type: DataTypes.DATEONLY,
    allowNull: true
  },
  due_date: {
    type: DataTypes.DATEONLY,
    allowNull: true
  }
}, {
  tableName: 'tasks',
  timestamps: true,
  createdAt: 'created_at',
  updatedAt: 'updated_at',
  indexes: [
    { fields: ['project_id'] },
    { fields: ['milestone_id'] },
    { fields: ['created_by'] },
    { fields: ['assigned_to'] },
    { fields: ['step_id'] }
  ]
});
