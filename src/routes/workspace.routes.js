import { Router } from "express";
import { createWorkspace, getMyWorkspace, addWorkspaceMember, deleteWorkspace } from "../controllers/workspace.controller.js";
import { authenticateJWT } from "../middleware/auth.middleware.js";
import { requireWorkspaceMember, requireWorkspaceRole } from "../middleware/workspace.middleware.js";

const WorkspaceRouter = Router();

WorkspaceRouter.post("/create", authenticateJWT, createWorkspace);

WorkspaceRouter.get("/mine", authenticateJWT, getMyWorkspace);

WorkspaceRouter.post("/:workspace_id/add", authenticateJWT, requireWorkspaceMember, requireWorkspaceRole(["Owner", "Admin"]), addWorkspaceMember);

WorkspaceRouter.delete("/:workspace_id/delete", authenticateJWT, requireWorkspaceMember, requireWorkspaceRole(["Owner"]), deleteWorkspace);

export default WorkspaceRouter;