import { DataTypes } from 'sequelize';
import sequelize from '../config/db.js';


export const ProjectMember = sequelize.define('ProjectMember', {
  project_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    primaryKey: true,
    references: { model: 'project', key: 'project_id' },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  },
  user_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    primaryKey: true,
    references: { model: 'user', key: 'user_id' },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  },
  role: {
    type: DataTypes.ENUM('Owner', 'Member', 'Viewer'),
    allowNull: false,
    defaultValue: 'Member'
  },
  joined_at: {
    type: DataTypes.DATE,
    allowNull: false,
    defaultValue: DataTypes.NOW
  }
}, {
  tableName: 'project_member',
  timestamps: false
});

export default ProjectMember;
