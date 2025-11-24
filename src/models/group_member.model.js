import { DataTypes } from 'sequelize';
import sequelize from '../config/db.js';

export const GroupMember = sequelize.define("GroupMember", {
  group_id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    allowNull: false
  },
  user_id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    allowNull: false
  },
  role: {
    type: DataTypes.ENUM("Owner", "Manager", "Member", "Viewer"),
    defaultValue: "Member",
    allowNull: false
  },
  joined_at: {
    type: DataTypes.DATE,
    defaultValue: DataTypes.NOW
  }
}, {
  tableName: "group_members",
  timestamps: false
});
