import { Router } from "express";
import { authenticateJWT } from "../middleware/auth.middleware.js";
import { createGroup, getGroupsByUser, removeGroup, addMemberToGroup } from "../controllers/group.controller.js";

const GroupRouter = Router();

GroupRouter.post("/create", authenticateJWT, createGroup);

GroupRouter.get("/user", authenticateJWT, getGroupsByUser);

GroupRouter.post("/add-member", authenticateJWT, addMemberToGroup);

GroupRouter.delete("/:group_id", authenticateJWT, removeGroup);
export default GroupRouter;