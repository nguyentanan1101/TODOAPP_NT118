import { DataTypes } from "sequelize";
import sequelize from "../config/db.js";

export const WorkspaceInvitation = sequelize.define(
  "WorkspaceInvitation",
  {
    invitation_id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    workspace_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: "workspaces",
        key: "workspace_id",
      },
      onUpdate: "CASCADE",
      onDelete: "CASCADE",
    },
    email: {
      type: DataTypes.STRING(100),
      allowNull: false,
    },
    role: {
      type: DataTypes.ENUM("Admin", "Member", "Viewer"),
      allowNull: false,
      defaultValue: "Member",
    },
    invited_by: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: "users",
        key: "user_id",
      },
      onUpdate: "CASCADE",
      onDelete: "CASCADE",
    },
    token: {
      type: DataTypes.STRING(255),
      allowNull: false,
      unique: true,
    },
    status: {
      type: DataTypes.ENUM("pending", "accepted", "expired"),
      defaultValue: "pending",
    },
    expires_at: {
      type: DataTypes.DATE,
      allowNull: false,
    },
    created_at: {
      type: DataTypes.DATE,
      defaultValue: DataTypes.NOW,
    },
  },
  {
    tableName: "workspace_invitations",
    timestamps: false,
  }
);

export const GroupInvitation = sequelize.define(
  "GroupInvitation",
  {
    invitation_id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    group_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: "groups",
        key: "group_id",
      },
      onUpdate: "CASCADE",
      onDelete: "CASCADE",
    },
    email: {
      type: DataTypes.STRING(100),
      allowNull: false,
    },
    role: {
      type: DataTypes.ENUM("Owner", "Manager", "Member", "Viewer"),
      defaultValue: "Member",
      allowNull: false,
    },
    invited_by: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: "users",
        key: "user_id",
      },
      onUpdate: "CASCADE",
      onDelete: "CASCADE",
    },
    token: {
      type: DataTypes.STRING(255),
      allowNull: false,
      unique: true,
    },
    status: {
      type: DataTypes.ENUM("pending", "accepted", "expired"),
      defaultValue: "pending",
    },
    expires_at: {
      type: DataTypes.DATE,
      allowNull: false,
    },
    created_at: {
      type: DataTypes.DATE,
      defaultValue: DataTypes.NOW,
    },
  },
  {
    tableName: "group_invitations",
    timestamps: false,
  }
);

export const ProjectInvitation = sequelize.define(
  "ProjectInvitation",
  {
    invitation_id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    project_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: "projects",
        key: "project_id",
      },
      onUpdate: "CASCADE",
      onDelete: "CASCADE",
    },
    email: {
      type: DataTypes.STRING(100),
      allowNull: false,
    },
    role: {
      type: DataTypes.ENUM("Owner", "Manager", "Contributor", "Viewer"),
      defaultValue: "Contributor",
      allowNull: false,
    },
    invited_by: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: "users",
        key: "user_id",
      },
      onUpdate: "CASCADE",
      onDelete: "CASCADE",
    },
    token: {
      type: DataTypes.STRING(255),
      allowNull: false,
      unique: true,
    },
    status: {
      type: DataTypes.ENUM("pending", "accepted", "expired"),
      defaultValue: "pending",
    },
    expires_at: {
      type: DataTypes.DATE,
      allowNull: false,
    },
    created_at: {
      type: DataTypes.DATE,
      defaultValue: DataTypes.NOW,
    },
  },
  {
    tableName: "project_invitations",
    timestamps: false,
  }
);
