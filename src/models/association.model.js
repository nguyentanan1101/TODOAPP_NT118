import { User } from './auth.model.js';
import { Group, GroupRole } from './group.model.js';
import { GroupMember } from './group_member.model.js';
import { RefreshToken } from './token.model.js';
import { Task, TaskStatus } from './task.model.js';
import { Project, ProjectStatus, ProjectRole } from './project.model.js';
import { ProjectMember } from './project_member.model.js';
import { PerformanceRecord } from './performance_record.model.js';
import { Subtask } from './subtask.model.js';
import { Workspace } from './workspace.model.js';
import { TaskHistory } from './task_history.model.js';

export default function initAssociations() {

  // Workspace associations
  Workspace.belongsTo(User, { foreignKey: 'owner_id', as: 'owner' });
  User.hasMany(Workspace, { foreignKey: 'owner_id', as: 'ownedWorkspaces' });

  // Group associations
  Group.hasMany(GroupMember, { foreignKey: 'group_id', as: 'members' });
  Group.hasMany(GroupMember, { foreignKey: 'group_id', as: 'membership' });
  GroupMember.belongsTo(Group, { foreignKey: 'group_id', as: 'group' });

  User.hasMany(GroupMember, { foreignKey: 'user_id', as: 'groupMemberships' });
  GroupMember.belongsTo(User, { foreignKey: 'user_id', as: 'user' });

  GroupMember.belongsTo(GroupRole, { foreignKey: 'group_role_id', as: 'role' });
  GroupRole.hasMany(GroupMember, { foreignKey: 'group_role_id', as: 'members' });

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

   Task.hasMany(Subtask, {
    foreignKey: 'task_id',
    as: 'subtasks',
    onDelete: 'CASCADE',
    onUpdate: 'CASCADE'
  });

  Subtask.belongsTo(Task, {
    foreignKey: 'task_id',
    as: 'task',
    onDelete: 'CASCADE',
    onUpdate: 'CASCADE'
  });


  
  Task.belongsTo(User, { foreignKey: 'created_by', as: 'creator' });
  User.hasMany(Task, { foreignKey: 'created_by', as: 'createdTasks' });

  Task.belongsTo(User, { foreignKey: 'assigned_to', as: 'assignee' });
  User.hasMany(Task, { foreignKey: 'assigned_to', as: 'assignedTasks' });

  Task.belongsTo(Project, { foreignKey: 'project_id', as: 'project' });
  Project.hasMany(Task, { foreignKey: 'project_id', as: 'tasks' });

  Task.belongsTo(TaskStatus, { foreignKey: 'task_status_id', as: 'status' });
  TaskStatus.hasMany(Task, { foreignKey: 'task_status_id', as: 'tasks' });

  Subtask.belongsTo(TaskStatus, { foreignKey: 'task_status_id', as: 'status' });
  TaskStatus.hasMany(Subtask, { foreignKey: 'task_status_id', as: 'subtasks' });

  Task.hasMany(TaskHistory, { foreignKey: 'task_id', as: 'history' });
  TaskHistory.belongsTo(Task, { foreignKey: 'task_id', as: 'task' });

  TaskHistory.belongsTo(User, { foreignKey: 'changed_by_user_id', as: 'changedBy' });
  User.hasMany(TaskHistory, { foreignKey: 'changed_by_user_id', as: 'taskHistoryChanges' });


  
  Project.hasMany(ProjectMember, { foreignKey: 'project_id', as: 'projectMembers' });
  ProjectMember.belongsTo(Project, { foreignKey: 'project_id', as: 'project' });

  User.hasMany(ProjectMember, { foreignKey: 'user_id', as: 'projectMemberships' });
  ProjectMember.belongsTo(User, { foreignKey: 'user_id', as: 'user' });

  ProjectMember.belongsTo(ProjectRole, { foreignKey: 'project_role_id', as: 'role' });
  ProjectRole.hasMany(ProjectMember, { foreignKey: 'project_role_id', as: 'members' });

  Project.belongsTo(ProjectStatus, { foreignKey: 'project_status_id', as: 'status' });
  ProjectStatus.hasMany(Project, { foreignKey: 'project_status_id', as: 'projects' });


  
  Project.belongsTo(User, { foreignKey: 'owner_id', as: 'owner' });
  User.hasMany(Project, { foreignKey: 'owner_id', as: 'ownedProjects' });

  Project.belongsTo(Group, { foreignKey: 'group_id', as: 'group' });
  Group.hasMany(Project, { foreignKey: 'group_id', as: 'projects' });

  User.belongsToMany(Project, {
    through: ProjectMember,
    foreignKey: 'user_id',
    otherKey: 'project_id',
    as: 'memberProjects'
  });

  Project.belongsToMany(User, {
    through: ProjectMember,
    foreignKey: 'project_id',
    otherKey: 'user_id',
    as: 'members'
  });

  PerformanceRecord.belongsTo(Group, { foreignKey: 'group_id', as: 'group' });
  Group.hasMany(PerformanceRecord, { foreignKey: 'group_id', as: 'performanceRecords' });

  
  PerformanceRecord.belongsTo(User, { foreignKey: 'user_id', as: 'member' });
  User.hasMany(PerformanceRecord, { foreignKey: 'user_id', as: 'receivedEvaluations' });

  
  PerformanceRecord.belongsTo(User, { foreignKey: 'created_by', as: 'evaluator' });
  User.hasMany(PerformanceRecord, { foreignKey: 'created_by', as: 'givenEvaluations' });

}
