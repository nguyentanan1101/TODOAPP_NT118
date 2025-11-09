import { User } from '../models/auth.model.js';
import { Project } from '../models/project.model.js';
import { ProjectMember } from '../models/project_member.model.js';
import { Group } from '../models/group.model.js';
import { Task } from '../models/task.model.js';


export async function createProjectService({ project_name, project_status, start_date, due_date, owner_id, group_id }) {
    if (!project_name || !owner_id || !group_id) {
        throw { status: 400, message: "Cần nhập đầy đủ Tên dự án, ID chủ sở hữu và ID nhóm" };
    }
    const group = await Group.findByPk(group_id);
    if (!group) {
        throw { status: 404, message: "Nhóm không tồn tại" };
    }
    if( start_date && due_date && new Date(start_date) > new Date(due_date)) {
        throw { status: 400, message: "Ngày bắt đầu không được sau ngày kết thúc" };
    }
    const project = await Project.create({ project_name, project_status, start_date, due_date, owner_id, group_id });
     await ProjectMember.create({
        project_id: project.project_id,
        user_id: owner_id,
        role: 'Owner'   
    });
    return project;
}


export async function getProjectsByOwnerService(owner_id) {
    const user = await User.findByPk(owner_id, {
        include: [{ model: Project, as: 'ownedProjects',
            include: [{ model: User, as: 'members', attributes: ['user_id', 'username', 'email'],
                through: { attributes: ['role'] }
             }] }]
    });
    return user ? user.ownedProjects : [];
}


export async function addProjectMemberService(project_id, user_id, role = 'Member') {
    const project = await Project.findByPk(project_id);
    if (!project) {
        throw { status: 404, message: "Dự án không tồn tại" };
    }
    const user = await User.findByPk(user_id);
    if (!user) {
        throw { status: 404, message: "Người dùng không tồn tại" };
    }
    const existingMember = await ProjectMember.findOne({ where: { project_id, user_id } });
    if (existingMember) {
        throw { status: 400, message: "Người dùng đã là thành viên của dự án này" };
    }
    const projectMember = await ProjectMember.create({ project_id, user_id, role });
    return projectMember;
}


export async function getTasksByProjectService(project_id) {
    const project = await Project.findByPk(project_id, {
        include: [{ model: Task, as: 'tasks',
            include: [
                { model: User, as: 'creator', attributes: ['user_id', 'username', 'email'] },
                { model: User, as: 'assignee', attributes: ['user_id', 'username', 'email'] }
            ] }]
    });
    return project ? project.tasks : [];
}

export async function assginTaskToProjectMemberService(task_id, project_id, user_id) {
    const projectMember = await ProjectMember.findOne({ where: { project_id, user_id } });
    if (!projectMember) {
        throw { status: 400, message: "Người dùng không phải là thành viên của dự án" };
    }   
    const task = await Task.findByPk(task_id);
    if (!task) {
        throw { status: 404, message: "Công việc không tồn tại" };
    }
    task.assigned_to = user_id;
    await task.save();
    return task;
}