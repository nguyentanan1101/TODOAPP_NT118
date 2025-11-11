import { viewSubtaskByTaskService, changeSubtaskStatusService } from "../services/subtaskservices.js";

export async function viewSubTaskByTask(req, res, next) {
    try {
        const { task_id } = req.params;
        const { status } = req.query;
        const subtasks = await viewSubtaskByTaskService(task_id, status);
        return res.status(200).json({ subtasks });
    } catch (err) {
        if (err && err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}

export async function changeSubtaskStatus(req, res, next) {
    try {
        const { subtask_id } = req.params;
        const { newStatus } = req.body;
        const updatedSubTask = await changeSubtaskStatusService(subtask_id, newStatus);
        return res.status(200).json({ subtask: updatedSubTask });
    } catch (err) {
        if (err && err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}
