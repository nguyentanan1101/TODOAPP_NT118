import { DataTypes } from 'sequelize';
import sequelize from '../config/db.js';


export const GroupMember = sequelize.define('GroupMember', {
  group_id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    allowNull: false,
    references: {
      model: 'groups',
      key: 'group_id'
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
  group_role_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'group_roles',
      key: 'group_role_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'RESTRICT'
  },
  joined_at: {
    type: DataTypes.DATE,
    defaultValue: DataTypes.NOW
  }
}, {
  tableName: 'group_members',
  timestamps: false,
  indexes: [
    {
      fields: ['group_id']
    },
    {
      fields: ['user_id']
    },
    {
      fields: ['group_role_id']
    }
  ]
});

export default GroupMember;