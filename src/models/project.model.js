import { DataTypes } from 'sequelize';
import sequelize from '../config/db.js';

export const Project = sequelize.define('Project', {
  project_id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  group_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'groups',
      key: 'group_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  },
  name: {
    type: DataTypes.STRING(255),
    allowNull: false
  },
  description: {
    type: DataTypes.TEXT,
    allowNull: true
  },
  status: {
    type: DataTypes.ENUM('Active', 'On Hold', 'Completed', 'Archived'),
    allowNull: false,
    defaultValue: 'Active'
  },
  owner_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'users',
      key: 'user_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'RESTRICT'
  },
  workflow_id: {
    type: DataTypes.INTEGER,
    allowNull: true,
    references: {
      model: 'workflows',
      key: 'workflow_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'SET NULL'
  },
  created_at: {
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
  tableName: 'projects',
  timestamps: false,
  indexes: [
    { fields: ['group_id'] },
    { fields: ['owner_id'] },
    { fields: ['workflow_id'] }
  ]
});
