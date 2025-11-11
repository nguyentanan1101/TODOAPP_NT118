import { viewTasksByUserService, changeTaskStatusService, updateTaskStatusIfSubtasksCompletedService } from "../services/taskservices.js";

export async function viewTasksByUser(req, res, next) {
    try {
        const user_id = req.user.id;
        const { status } = req.query;
        const tasks = await viewTasksByUserService(user_id, status);
        return res.status(200).json({ tasks });
    } catch (err) {
        if (err && err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    } 
}


export async function changeTaskStatus(req, res, next) {
    try {
        const { task_id } = req.params;
        const { newStatus } = req.body;
        const updatedTask = await changeTaskStatusService(task_id, newStatus);
        return res.status(200).json({ task: updatedTask });
    } catch (err) {
        if (err && err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }  
}

export async function updateTaskStatusIfSubtasksCompleted(req, res, next) {
     try {
        const { task_id } = req.params;
        const updatedTask = await updateTaskStatusIfSubtasksCompletedService(task_id);
        return res.status(200).json({ task: updatedTask });
    } catch (err) {
        if (err && err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}
