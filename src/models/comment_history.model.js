import { DataTypes } from 'sequelize';
import sequelize from '../config/db.js';

export const CommentHistory = sequelize.define('CommentHistory', {
  comment_history_id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  comment_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'comment',
      key: 'comment_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'CASCADE'
  },
  edited_by_user_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: {
      model: 'users',
      key: 'user_id'
    },
    onUpdate: 'CASCADE',
    onDelete: 'RESTRICT'
  },
  old_content: {
    type: DataTypes.TEXT,
    allowNull: false
  },
  edited_at: {
    type: DataTypes.DATE,
    defaultValue: DataTypes.NOW
  }
}, {
  tableName: 'comment_history',
  timestamps: false,
  indexes: [
    { fields: ['comment_id'] },
    { fields: ['edited_by_user_id'] }
  ]
});
