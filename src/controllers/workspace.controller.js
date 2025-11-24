import { createWorkSpaceService, addWorkspaceMemberService, getWorkspaceByOwnerService, deleteWorkspaceService, getListMemberService, removeWorkspaceMemberService, updateWorkspaceMemberRoleService, leaveWorkspaceService } from "../services/workspaceservices.js";

export async function createWorkspace(req, res, next) {
    try {
        const owner_id = req.user.id;
        const { name, description } = req.body;
        const workspace = await createWorkSpaceService({ name, description, owner_id });
         return res.status(201).json({
            message: "Tạo workspace thành công",
            workspace
        });

    } catch(err) {
        if (err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}

export async function getMyWorkspace(req, res, next) {
    try {
        const owner_id = req.user.id;
        const workspace = await getWorkspaceByOwnerService(owner_id);
        if (!workspace) {
            return res.status(404).json({ message: "Bạn chưa tạo workspace nào" });
        }
        return res.status(200).json(workspace);
    } catch (err) {
        if (err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}

export async function addWorkspaceMember(req, res, next) {
    try {
        const { workspace_id } = req.params;
        const { user_id, role } = req.body;
        const requesterRole = req.workspaceRole;
        const member = await addWorkspaceMemberService(
            workspace_id,
            user_id,
            role,
            requesterRole
        );
        return res.status(201).json({
            message: "Thêm thành viên vào workspace thành công",
            member
        });
    } catch (err) {
        if (err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}

export async function getListMember(req, res, next) {
    try {
        const { workspace_id } = req.params;
        const list = await getListMemberService( workspace_id );
        return res.status(201).json(list);
    }
    catch (err) {
        if (err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}


export async function removeWorkspaceMemberService(req, res, next) {
    try {
        const { workspace_id, user_id } = req.params;

        const result = await removeWorkspaceMemberService(workspace_id, user_id);
        return res.status(200).json(result);
    }
    catch (err) {
        if (err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}

export async function updateWorkspaceMemberRole(req, res, next) {
    try {
        const requesterRole = req.workspaceRole;
        const { workspace_id, user_id } = req.params;
        const { role } = req.body;

        const result = await updateWorkspaceMemberRoleService(workspace_id, user_id, role, requesterRole);
        return res.status(200).json(result);
    }
     catch (err) {
        if (err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}

export async function leaveWorkspace(req, res, next) {
    try {
        const { workspace_id } = req.params;
        const user_id = req.user.id;

        const result = await leaveWorkspaceService(workspace_id, user_id);
        return res.status(200).json(result);
    } catch (err) {
        next(err);
    }
}


export async function deleteWorkspace(req, res, next) {
    try {
        const { workspace_id } = req.params;
        const result = await deleteWorkspaceService(workspace_id);
        return res.status(200).json(result);
    } catch (err) {
        if (err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}