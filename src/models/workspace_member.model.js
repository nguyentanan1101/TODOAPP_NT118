import { DataTypes } from 'sequelize';
import sequelize from '../config/db.js';

export const WorkspaceMember = sequelize.define('WorkspaceMember', {
  workspace_id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    allowNull: false,
    references: {
      model: 'workspaces',
      key: 'workspace_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  },
  user_id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    allowNull: false,
    references: {
      model: 'users',
      key: 'user_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  },
  role: {
    type: DataTypes.ENUM('Owner', 'Admin', 'Member', 'Viewer'),
    allowNull: false,
    defaultValue: 'Member'
  },
  joined_at: {
    type: DataTypes.DATE,
    defaultValue: DataTypes.NOW
  }
}, {
  tableName: 'workspace_members',
  timestamps: false,
  indexes: [
    { fields: ['workspace_id'] },
    { fields: ['user_id'] }
  ]
});
