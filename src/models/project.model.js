import { DataTypes, Sequelize } from 'sequelize';
import sequelize from '../config/db.js';

export const Project = sequelize.define('Project', {
  project_id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  project_name: {
    type: DataTypes.STRING(100),
    allowNull: false
  },
  project_status: {
    type: DataTypes.ENUM('Working', 'Done'),
    allowNull: false,
    defaultValue: 'Working'
  },
  created_date: {
    type: DataTypes.DATEONLY,
    allowNull: false,
    defaultValue: Sequelize.literal('CURRENT_DATE')
  },
  start_date: {
    type: DataTypes.DATEONLY,
    allowNull: true
  },
  due_date: {
    type: DataTypes.DATEONLY,
    allowNull: true
  },
  owner_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'user',
      key: 'user_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  }
}, {
  tableName: 'project',
  timestamps: false
});
export default Project;

