import { Router } from "express";
import { authenticateJWT } from "../middleware/auth.middleware.js";
import { viewTasksByUser, changeTaskStatus } from "../controllers/task.controller.js";

const TaskRouter = Router();

TaskRouter.get("/user-tasks", authenticateJWT, viewTasksByUser);
TaskRouter.patch("/:task_id/status", authenticateJWT, changeTaskStatus);

export default TaskRouter;