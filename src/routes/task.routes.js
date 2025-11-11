import { Router } from "express";
import { authenticateJWT } from "../middleware/auth.middleware.js";
import { viewTasksByUser, changeTaskStatus,updateTaskStatusIfSubtasksCompleted } from "../controllers/task.controller.js";

const TaskRouter = Router();

TaskRouter.get("/user-tasks", authenticateJWT, viewTasksByUser);

TaskRouter.patch("/:task_id/status", authenticateJWT, changeTaskStatus);

TaskRouter.patch("/:task_id/update-completed", authenticateJWT, updateTaskStatusIfSubtasksCompleted);

export default TaskRouter;