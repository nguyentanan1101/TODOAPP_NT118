import { DataTypes, Sequelize } from 'sequelize';
import sequelize from '../config/db.js';


export const Subtask = sequelize.define('Subtask', {
  subtask_id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  subtask_name: {
    type: DataTypes.STRING(100),
    allowNull: false
  },
  subtask_description: {
    type: DataTypes.TEXT,
    allowNull: true
  },
  subtask_status: {
    type: DataTypes.ENUM('Working', 'Done'),
    allowNull: false,
    defaultValue: 'Working'
  },
  created_date: {
    type: DataTypes.DATEONLY,
    allowNull: false,
    defaultValue: Sequelize.literal('CURRENT_DATE')
  },
  task_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'task',
      key: 'task_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  }
}, {
  tableName: 'subtask',
  timestamps: false
});



export default Subtask;