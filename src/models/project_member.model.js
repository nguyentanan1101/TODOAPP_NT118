import { DataTypes } from "sequelize";
import sequelize from "../config/db.js";

export const ProjectMember = sequelize.define("ProjectMember", {
  project_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    primaryKey: true
  },
  user_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
    primaryKey: true
  },
  role: {
    type: DataTypes.ENUM("Owner", "Manager", "Contributor", "Viewer"),
    allowNull: false,
    defaultValue: "Contributor"
  },
  joined_at: {
    type: DataTypes.DATE,
    defaultValue: DataTypes.NOW
  }
}, {
  tableName: "project_members",
  timestamps: false,
  indexes: [
    { fields: ["project_id"] },
    { fields: ["user_id"] }
  ]
});
