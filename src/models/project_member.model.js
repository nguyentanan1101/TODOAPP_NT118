import { DataTypes } from 'sequelize';
import sequelize from '../config/db.js';


export const ProjectMember = sequelize.define('ProjectMember', {
  project_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    primaryKey: true,
    references: {
      model: 'projects',
      key: 'project_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  },
  user_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    primaryKey: true,
    references: {
      model: 'users',
      key: 'user_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  },
  project_role_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'project_roles',
      key: 'project_role_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'RESTRICT'
  },
  joined_at: {
    type: DataTypes.DATE,
    defaultValue: DataTypes.NOW
  }
}, {
  tableName: 'project_members',
  timestamps: false,
  indexes: [
    {
      fields: ['project_id']
    },
    {
      fields: ['user_id']
    },
    {
      fields: ['project_role_id']
    }
  ]
});

export default ProjectMember;
