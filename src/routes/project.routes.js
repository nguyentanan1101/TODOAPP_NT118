import { Router } from "express";
import { authenticateJWT } from "../middleware/auth.middleware.js";
import { createProject, getProjectsByOwner, addProjectMember, getTasksByProject, assignTaskToProjectMember } from "../controllers/project.controller.js";

const ProjectRouter = Router();

ProjectRouter.post("/group/:group_id/create", authenticateJWT, createProject);

ProjectRouter.get("/owner", authenticateJWT, getProjectsByOwner);

ProjectRouter.post("/member", authenticateJWT, addProjectMember);

ProjectRouter.get("/:project_id/tasks", authenticateJWT, getTasksByProject);

ProjectRouter.post("/assign-task", authenticateJWT, assignTaskToProjectMember);


export default ProjectRouter;

