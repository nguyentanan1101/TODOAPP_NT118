import { createProjectService, getProjectsByOwnerService, addProjectMemberService, assginTaskToProjectMemberService, getTasksByProjectService } from '../services/projectservices.js'
import { getUserIdByEmailService } from '../services/authservices.js';


export async function createProject(req, res, next) {
    try {
        const { project_name, project_status, start_date, due_date } = req.body;
        const owner_id = req.user.id;
        const group_id = req.params.group_id;
        const newProject = await createProjectService({ project_name, project_status, start_date, due_date, owner_id, group_id });
        return res.status(201).json({
            message: "Dự án đã được tạo thành công",
            project: newProject
        });
    } catch (err) {
        if (err && err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}

export async function getProjectsByOwner(req, res, next) {
    try {
        const owner_id = req.user.id;
        const projects = await getProjectsByOwnerService(owner_id);
        return res.status(200).json({ projects });
    } catch (err) {
        if (err && err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}


export async function addProjectMember(req, res, next) {
    try {
        const { project_id, email, role } = req.body;
        const user_id = await getUserIdByEmailService(email);
        const projectMember = await addProjectMemberService(project_id, user_id, role);
        return res.status(201).json({
            message: "Thành viên đã được thêm vào dự án",
            projectMember
        });
    } catch (err) {
        if (err && err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}

export async function assignTaskToProjectMember(req, res, next) {
    try {
        const { task_id, project_id, email } = req.body;
        const user_id = await getUserIdByEmailService(email);
        const updatedTask = await assginTaskToProjectMemberService(task_id, project_id, user_id);
        return res.status(200).json({
            message: "Công việc đã được giao cho thành viên dự án",
            task: updatedTask
        });
    } catch (err) {
        if (err && err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}

export async function getTasksByProject(req, res, next) {
    try {
        const project_id = req.params.project_id;
        const tasks = await getTasksByProjectService(project_id);
        return res.status(200).json({ tasks });
    } catch (err) {
        if (err && err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}