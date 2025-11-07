import { User } from './auth.model.js';
import { Group } from './group.model.js';
import { GroupMember } from './group_member.model.js';
import { RefreshToken } from './token.model.js';
import { Task } from './task.model.js';
import { Project } from './project.model.js';
import { ProjectMember } from './project_member.model.js';

export default function initAssociations() {

  
  Group.hasMany(GroupMember, { foreignKey: 'group_id', as: 'members' });
  Group.hasMany(GroupMember, { foreignKey: 'group_id', as: 'membership' });  
  GroupMember.belongsTo(Group, { foreignKey: 'group_id', as: 'group' });

  User.hasMany(GroupMember, { foreignKey: 'user_id', as: 'groupMemberships' });
  GroupMember.belongsTo(User, { foreignKey: 'user_id', as: 'user' });

 
  User.belongsToMany(Group, {
    through: GroupMember,
    foreignKey: 'user_id',
    otherKey: 'group_id',
    as: 'groups'
  });
  Group.belongsToMany(User, {
    through: GroupMember,
    foreignKey: 'group_id',
    otherKey: 'user_id',
    as: 'users'
  });

  
  RefreshToken.belongsTo(User, { foreignKey: 'user_id', as: 'user' });
  User.hasMany(RefreshToken, { foreignKey: 'user_id', as: 'refreshTokens' });

  Task.belongsTo(User, { foreignKey: 'created_by', as: 'creator' });
  User.hasMany(Task, { foreignKey: 'created_by', as: 'createdTasks' });

  Task.belongsTo(User, { foreignKey: 'assigned_to', as: 'assignee' });
  User.hasMany(Task, { foreignKey: 'assigned_to', as: 'assignedTasks' });

  Task.belongsTo(Project, { foreignKey: 'project_id', as: 'project' });
  Project.hasMany(Task, { foreignKey: 'project_id', as: 'tasks' });

  Project.hasMany(ProjectMember, { foreignKey: 'project_id', as: 'projectMembers' });
  ProjectMember.belongsTo(Project, { foreignKey: 'project_id', as: 'project' });

  User.hasMany(ProjectMember, { foreignKey: 'user_id', as: 'projectMemberships' });
  ProjectMember.belongsTo(User, { foreignKey: 'user_id', as: 'user' });
}
