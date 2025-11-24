import { DataTypes } from 'sequelize';
import sequelize from '../config/db.js';

export const Workspace = sequelize.define('Workspace', {
  workspace_id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  name: {
    type: DataTypes.STRING(100),
    allowNull: false
  },
  description: {
    type: DataTypes.TEXT,
    allowNull: true
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
  created_at: {
    type: DataTypes.DATE,
    defaultValue: DataTypes.NOW
  }
}, {
  tableName: 'workspaces',
  timestamps: false,
  indexes: [
    { fields: ['owner_id'] }
  ]
});
