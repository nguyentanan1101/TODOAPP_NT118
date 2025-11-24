import { DataTypes } from 'sequelize';
import sequelize from '../config/db.js';

export const Workflow = sequelize.define('Workflow', {
  workflow_id: {
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
    type: DataTypes.STRING(100),
    allowNull: false
  },
  description: {
    type: DataTypes.TEXT,
    allowNull: true
  }
}, {
  tableName: 'workflows',
  timestamps: false,
  indexes: [
    { fields: ['group_id'] }
  ]
});
