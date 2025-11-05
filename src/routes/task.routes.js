import { Router } from "express";
import { CreateTask,UpdateTask,DeleteTask, GetAllTasks } from "../controllers/task.controller.js";
const TaskRouter = Router();

TaskRouter.post("/", CreateTask);

TaskRouter.put ("/update", UpdateTask);

TaskRouter.delete("/:id", DeleteTask);

TaskRouter.get ("/all", GetAllTasks);

export default TaskRouter;