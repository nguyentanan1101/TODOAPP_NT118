import { GroupMember } from "../models/group_member.model.js";

export const getGroupRole = async (user_id, group_id) => {
    return await GroupMember.findOne({
        where: { user_id, group_id }
    });
};

export const requireGroupMember = async (req, res, next) => {
    const user_id = req.user.user_id;
    const group_id = Number(req.params.group_id || req.body.group_id);

    const member = await getGroupRole(user_id, group_id);
    if (!member)
        return res.status(403).json({ message: "Bạn không thuộc group này" });

    req.groupRole = member.role;
    next();
};

export const requireGroupRole = (roles = []) => {
    return async (req, res, next) => {
        if (!roles.includes(req.groupRole)) {
            return res.status(403).json({ message: "Không đủ quyền tại group" });
        }
        next();
    };
};
