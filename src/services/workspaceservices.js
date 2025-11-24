import { Workspace } from '../models/workspace.model.js';
import { User } from '../models/auth.model.js';
import { WorkspaceMember } from '../models/workspace_member.model.js';


export async function createWorkSpaceService({ name, description, owner_id }) {
    if (!name || !owner_id) {
        throw { status: 400, message: "Cần nhập đầy đủ Tên workspace và ID chủ sở hữu" };
    }
    const workspace = await Workspace.create({ name, description, owner_id });
    await WorkspaceMember.create({ workspace_id: workspace.workspace_id, user_id: owner_id, role: "Owner" });
    return workspace;
}

export async function getWorkspaceByOwnerService(owner_id) {
    const workspace = await Workspace.findOne({ where: { owner_id } });
    return workspace;
}


export async function addWorkspaceMemberService( workspace_id, user_id, role = "Member", requesterRole ) {
    const VALID_ROLES = ["Admin", "Member", "Viewer"];
    if (role === "Owner") {
        throw { status: 403, message: "Không thể gán vai trò Owner cho người khác" };
    }

    if (!VALID_ROLES.includes(role)) {
        throw { status: 400, message: "Vai trò không hợp lệ" };
    }
    if (role === "Admin" && requesterRole !== "Owner") {
        throw { status: 403, message: "Chỉ Owner mới được thêm Admin" };
    }

    const workspace = await Workspace.findByPk(workspace_id);
    if (!workspace) {
        throw { status: 404, message: "Workspace không tồn tại" };
    }

    const user = await User.findByPk(user_id);
    if (!user) {
        throw { status: 404, message: "Người dùng không tồn tại" };
    }
    const existed = await WorkspaceMember.findOne({ where: { workspace_id, user_id } });
    if (existed) {
        throw { status: 400, message: "Người dùng đã là thành viên của workspace này" };
    }
    const workspaceMember = await WorkspaceMember.create({
        workspace_id,
        user_id,
        role
    });

    return workspaceMember;
}

export async function getListMemberService(workspace_id) {
    return await WorkspaceMember.findAll({
        where: { workspace_id },
        include: [{
            model: User,
            attributes: ["user_id", "email", "username"]
        }]
    });
}

export async function removeWorkspaceMemberService(workspace_id, user_id) {

    const member = await WorkspaceMember.findOne({ where: { workspace_id, user_id } });

    if (!member) throw { status: 404, message: "Thành viên không tồn tại trong workspace" };

    if (member.role === "Owner") {
        throw { status: 403, message: "Không được xóa Owner khỏi workspace" };
    }

    await WorkspaceMember.destroy({ where: { workspace_id, user_id } });

    return { message: "Xóa thành viên thành công" };
}

export async function updateWorkspaceMemberRoleService(workspace_id, user_id, newRole, requesterRole) {

    const VALID_ROLES = ["Admin", "Member", "Viewer"];
    if (!VALID_ROLES.includes(newRole)) {
        throw { status: 400, message: "Role không hợp lệ" };
    }

    if (newRole === "Admin" && requesterRole !== "Owner") {
        throw { status: 403, message: "Chỉ Owner mới được gán Admin" };
    }

    const member = await WorkspaceMember.findOne({ where: { workspace_id, user_id } });

    if (!member) throw { status: 404, message: "Thành viên không tồn tại trong workspace" };

    if (member.role === "Owner") {
        throw { status: 403, message: "Không thể đổi role của Owner" };
    }

    member.role = newRole;
    await member.save();

    return { message: "Cập nhật role thành công", member };
}
export async function leaveWorkspaceService(workspace_id, user_id) {
    const member = await WorkspaceMember.findOne({ where: { workspace_id, user_id } });

    if (!member) throw { status: 404, message: "Bạn không thuộc workspace này" };

    if (member.role === "Owner") {
        throw { status: 403, message: "Owner không thể tự rời workspace" };
    }

    await WorkspaceMember.destroy({ where: { workspace_id, user_id } });

    return { message: "Rời workspace thành công" };
}


export async function deleteWorkspaceService(workspace_id) {
    await Workspace.destroy({
        where: { workspace_id }
    });

    return { message: "Xóa workspace thành công" };
}



