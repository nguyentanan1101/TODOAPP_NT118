import { User } from './auth.model.js';
import { Group } from './group.model.js';
import { GroupMember } from './group_member.model.js';
import { RefreshToken } from './token.model.js';
import { Task } from './task.model.js';
import { Project } from './project.model.js';
import { ProjectMember } from './project_member.model.js';
import { PerformanceRecord } from './performance_record.model.js';
import { Subtask } from './subtask.model.js';
import { Workspace } from './workspace.model.js';
import { WorkspaceMember } from './workspace_member.model.js';
import { TaskHistory } from './task_history.model.js';

import { Milestone } from './milestone.model.js';
import { Workflow } from './workflow.model.js';
import { WorkflowStep } from './workflow_step.model.js';
import { Comment } from './comment.model.js';
import { CommentHistory } from './comment_history.model.js';
import { WorkspaceInvitation, GroupInvitation, ProjectInvitation } from './invitation.model.js';

export default function initAssociations() {

  Workspace.belongsTo(User, { foreignKey: 'owner_id', as: 'owner' });
  User.hasMany(Workspace, { foreignKey: 'owner_id', as: 'ownedWorkspaces' });

  Workspace.hasMany(WorkspaceMember, { foreignKey: 'workspace_id', as: 'members' });
  WorkspaceMember.belongsTo(Workspace, { foreignKey: 'workspace_id', as: 'workspace' });

  User.hasMany(WorkspaceMember, { foreignKey: 'user_id', as: 'workspaceMemberships' });
  WorkspaceMember.belongsTo(User, { foreignKey: 'user_id', as: 'user' });

  User.belongsToMany(Workspace, {
    through: WorkspaceMember,
    foreignKey: 'user_id',
    otherKey: 'workspace_id',
    as: 'workspaces'
  });
  Workspace.belongsToMany(User, {
    through: WorkspaceMember,
    foreignKey: 'workspace_id',
    otherKey: 'user_id',
    as: 'users'
  });


  Group.belongsTo(Workspace, { foreignKey: 'workspace_id', as: 'workspace' });
  Workspace.hasMany(Group, { foreignKey: 'workspace_id', as: 'groups' });

  Group.hasMany(GroupMember, { foreignKey: 'group_id', as: 'members' });
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

  Workflow.belongsTo(Group, { foreignKey: 'group_id', as: 'group' });
  Group.hasMany(Workflow, { foreignKey: 'group_id', as: 'workflows' });

  WorkflowStep.belongsTo(Workflow, { foreignKey: 'workflow_id', as: 'workflow' });
  Workflow.hasMany(WorkflowStep, { foreignKey: 'workflow_id', as: 'steps' });

  Project.belongsTo(User, { foreignKey: 'owner_id', as: 'owner' });
  User.hasMany(Project, { foreignKey: 'owner_id', as: 'ownedProjects' });

  Project.belongsTo(Group, { foreignKey: 'group_id', as: 'group' });
  Group.hasMany(Project, { foreignKey: 'group_id', as: 'projects' });

  Project.belongsTo(Workflow, { foreignKey: 'workflow_id', as: 'workflow' });
  Workflow.hasMany(Project, { foreignKey: 'workflow_id', as: 'projects' });

  Project.hasMany(ProjectMember, { foreignKey: 'project_id', as: 'projectMembers' });
  ProjectMember.belongsTo(Project, { foreignKey: 'project_id', as: 'project' });

  User.hasMany(ProjectMember, { foreignKey: 'user_id', as: 'projectMemberships' });
  ProjectMember.belongsTo(User, { foreignKey: 'user_id', as: 'user' });

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


  Milestone.belongsTo(Project, { foreignKey: 'project_id', as: 'project' });
  Project.hasMany(Milestone, { foreignKey: 'project_id', as: 'milestones' });

  Task.belongsTo(User, { foreignKey: 'created_by', as: 'creator' });
  User.hasMany(Task, { foreignKey: 'created_by', as: 'createdTasks' });

  Task.belongsTo(User, { foreignKey: 'assigned_to', as: 'assignee' });
  User.hasMany(Task, { foreignKey: 'assigned_to', as: 'assignedTasks' });

  Task.belongsTo(Project, { foreignKey: 'project_id', as: 'project' });
  Project.hasMany(Task, { foreignKey: 'project_id', as: 'tasks' });

  Task.belongsTo(Milestone, { foreignKey: 'milestone_id', as: 'milestone' });
  Milestone.hasMany(Task, { foreignKey: 'milestone_id', as: 'tasks' });

  Task.belongsTo(WorkflowStep, { foreignKey: 'step_id', as: 'step' });
  WorkflowStep.hasMany(Task, { foreignKey: 'step_id', as: 'tasks' });

  Task.hasMany(Subtask, { foreignKey: 'task_id', as: 'subtasks' });
  Subtask.belongsTo(Task, { foreignKey: 'task_id', as: 'task' });

  TaskHistory.belongsTo(Task, { foreignKey: 'task_id', as: 'task' });
  Task.hasMany(TaskHistory, { foreignKey: 'task_id', as: 'history' });

  TaskHistory.belongsTo(User, { foreignKey: 'changed_by_user_id', as: 'changedBy' });
  User.hasMany(TaskHistory, { foreignKey: 'changed_by_user_id', as: 'taskHistoryChanges' });

  Comment.belongsTo(Task, { foreignKey: 'task_id', as: 'task' });
  Task.hasMany(Comment, { foreignKey: 'task_id', as: 'comments' });

  Comment.belongsTo(User, { foreignKey: 'user_id', as: 'author' });
  User.hasMany(Comment, { foreignKey: 'user_id', as: 'comments' });

  CommentHistory.belongsTo(Comment, { foreignKey: 'comment_id', as: 'comment' });
  Comment.hasMany(CommentHistory, { foreignKey: 'comment_id', as: 'history' });

  CommentHistory.belongsTo(User, { foreignKey: 'edited_by_user_id', as: 'editor' });
  User.hasMany(CommentHistory, { foreignKey: 'edited_by_user_id', as: 'editedComments' });

  RefreshToken.belongsTo(User, { foreignKey: 'user_id', as: 'user' });
  User.hasMany(RefreshToken, { foreignKey: 'user_id', as: 'refreshTokens' });

  PerformanceRecord.belongsTo(Group, { foreignKey: 'group_id', as: 'group' });
  Group.hasMany(PerformanceRecord, { foreignKey: 'group_id', as: 'performanceRecords' });

  PerformanceRecord.belongsTo(User, { foreignKey: 'user_id', as: 'member' });
  User.hasMany(PerformanceRecord, { foreignKey: 'user_id', as: 'receivedEvaluations' });

  PerformanceRecord.belongsTo(User, { foreignKey: 'created_by', as: 'evaluator' });
  User.hasMany(PerformanceRecord, { foreignKey: 'created_by', as: 'givenEvaluations' });

  WorkspaceInvitation.belongsTo(Workspace, {
  foreignKey: "workspace_id",
  as: "workspace"
  });
  Workspace.hasMany(WorkspaceInvitation, {
  foreignKey: "workspace_id",
  as: "invitations"
  });

  WorkspaceInvitation.belongsTo(User, {
  foreignKey: "invited_by",
  as: "inviter"
  });
  User.hasMany(WorkspaceInvitation, {
  foreignKey: "invited_by",
  as: "sentWorkspaceInvitations"
  });

  GroupInvitation.belongsTo(Group, {
  foreignKey: "group_id",
  as: "group"
  });
  Group.hasMany(GroupInvitation, {
    foreignKey: "group_id",
    as: "invitations"
  });

  GroupInvitation.belongsTo(User, {
    foreignKey: "invited_by",
    as: "inviter"
  });
  User.hasMany(GroupInvitation, {
    foreignKey: "invited_by",
    as: "sentGroupInvitations"
  });

  ProjectInvitation.belongsTo(Project, {
  foreignKey: "project_id",
  as: "project"
  });
  Project.hasMany(ProjectInvitation, {
    foreignKey: "project_id",
    as: "invitations"
  });

  ProjectInvitation.belongsTo(User, {
    foreignKey: "invited_by",
    as: "inviter"
  });
  User.hasMany(ProjectInvitation, {
    foreignKey: "invited_by",
    as: "sentProjectInvitations"
  });
}
