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
  project_status_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'project_status',
      key: 'project_status_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'RESTRICT'
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
    {
      fields: ['group_id']
    },
    {
      fields: ['owner_id']
    },
    {
      fields: ['project_status_id']
    },
    {
      fields: ['workflow_id']
    }
  ]
});

export const ProjectStatus = sequelize.define('ProjectStatus', {
  project_status_id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  name: {
    type: DataTypes.STRING(100),
    allowNull: false,
    unique: true
  },
  description: {
    type: DataTypes.STRING(255),
    allowNull: true
  }
}, {
  tableName: 'project_status',
  timestamps: false
});

export const ProjectRole = sequelize.define('ProjectRole', {
  project_role_id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  name: {
    type: DataTypes.STRING(50),
    allowNull: false,
    unique: true
  }
}, {
  tableName: 'project_roles',
  timestamps: false
});

export default Project;
