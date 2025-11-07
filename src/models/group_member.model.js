import { DataTypes } from 'sequelize';
import sequelize from '../config/db.js';


export const GroupMember = sequelize.define('GroupMember', {
  group_id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    allowNull: false,
    references: {
      model: 'group_member',
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
      model: 'user',
      key: 'user_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  },
  role: {
    type: DataTypes.ENUM('Owner', 'Member'),
    defaultValue: 'Member'
  },
  joined_at: {
    type: DataTypes.DATE,
    defaultValue: DataTypes.NOW
  }
}, {
  tableName: 'group_member',
  timestamps: false,
  indexes: [
    {
      fields: ['group_id']
    },
    {
      fields: ['user_id']
    }
  ]
});

export default GroupMember;