import { DataTypes } from 'sequelize';
import sequelize from '../config/db.js';

export const WorkflowStep = sequelize.define('WorkflowStep', {
  step_id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  workflow_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'workflows',
      key: 'workflow_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  },
  name: {
    type: DataTypes.STRING(100),
    allowNull: false
  },
  step_order: {
    type: DataTypes.INTEGER,
    allowNull: false
  }
}, {
  tableName: 'workflow_steps',
  timestamps: false,
  indexes: [
    { fields: ['workflow_id'] }
  ]
});
