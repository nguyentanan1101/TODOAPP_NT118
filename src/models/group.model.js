import { DataTypes } from "sequelize";
import sequelize from "../config/db.js";

export const Group = sequelize.define("Group", {
  group_id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true
  },
  workspace_id: {
    type: DataTypes.INTEGER,
    allowNull: false
  },
  name: {                               
    type: DataTypes.STRING(100),
    allowNull: false
  },
  description: {
    type: DataTypes.TEXT,
    allowNull: true
  },
  created_at: {
    type: DataTypes.DATE,
    defaultValue: DataTypes.NOW
  }
}, {
  tableName: "groups",
  timestamps: false
});
