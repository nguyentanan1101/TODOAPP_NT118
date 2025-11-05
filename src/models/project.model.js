import { DataTypes } from 'sequelize';
import sequelize from '../config/db.js';
import { User } from './auth.model.js';

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
    defaultValue: 'Working'
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
  owner_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: User,
      key: 'user_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  }
}, {
  tableName: 'project',
  timestamps: false
});

Project.belongsTo(User, {
  foreignKey: 'owner_id',
  as: 'owner'
});

export async function createProject(projectData) {
  return await Project.create(projectData);
}

export async function getProjectById(project_id) {
  return await Project.findByPk(project_id);
}

export async function getAllProjects() {
  return await Project.findAll();
}