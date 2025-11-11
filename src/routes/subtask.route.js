import { Router } from "express";
import { authenticateJWT } from "../middleware/auth.middleware.js";
import { viewSubTaskByTask, changeSubtaskStatus } from "../controllers/subtask.controller.js";


const SubTaskRouter = Router();

SubTaskRouter.get("/task/:task_id", authenticateJWT, viewSubTaskByTask);


SubTaskRouter.patch("/:subtask_id/status", authenticateJWT, changeSubtaskStatus);

export default SubTaskRouter;