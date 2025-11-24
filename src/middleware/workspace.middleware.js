import { WorkspaceMember } from "../models/workspace_member.model.js";

export const getWorkspaceRole = async (user_id, workspace_id) => {
    return await WorkspaceMember.findOne({
        where: { user_id, workspace_id }
    });
};

export const requireWorkspaceMember = async (req, res, next) => {
    const user_id = req.user.id;
    const workspace_id = Number(req.params.workspace_id || req.body.workspace_id);

    const member = await getWorkspaceRole(user_id, workspace_id);
    if (!member)
        return res.status(403).json({ message: "Bạn không thuộc workspace này" });

    req.workspaceRole = member.role;
    next();
};

export const requireWorkspaceRole = (roles = []) => {
    return async (req, res, next) => {
        if (!roles.includes(req.workspaceRole)) {
            return res.status(403).json({ message: "Không đủ quyền tại workspace" });
        }
        next();
    };
};