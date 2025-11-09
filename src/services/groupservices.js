import { Group } from '../models/group.model.js';
import { User } from '../models/auth.model.js';
import { GroupMember } from '../models/group_member.model.js';

export async function createGroupService({ group_name, description, owner_id }) {
  if (!group_name || !owner_id) {
    throw { status: 400, message: "Cần nhập đầy đủ Tên nhóm và ID chủ sở hữu" };
  }
    const group = await Group.create({ group_name, description });
    await GroupMember.create({  
        group_id: group.group_id,
        user_id: owner_id,
        role: 'Owner'
    });
    return group;
}

export async function getGroupByUserService(user_id) {
  const groups = await Group.findAll({
    include: [
      {
        model: GroupMember,
        as: 'membership',
        where: { user_id },   
        attributes: [],       
        required: true        
      },
      {
        model: GroupMember,
        as: 'members',
        include: [
          { model: User, as: 'user', attributes: ['user_id','username' ,'email'] }
        ],
        required: false     
      }
    ]
  });

  return groups;
}

export async function addMemberToGroupService({ group_id, user_id, role }) {
    const group = await Group.findByPk(group_id);
    if (!group) {
        throw { status: 404, message: "Nhóm không tồn tại" };
    }
    const userRole = role || "Member";

    const user = await User.findByPk(user_id);
    if (!user) {
        throw { status: 404, message: "Người dùng không tồn tại" };
    }

    await GroupMember.create({
        group_id,
        user_id,
        role: userRole
    });

    return { message: "Thêm thành viên vào nhóm thành công" };
}


export async function removeGroupService(group_id) {
    const group = await Group.findByPk(group_id); 
    if (!group) {
        throw { status: 404, message: "Nhóm không tồn tại" };
    }

    await GroupMember.destroy({ where: { group_id } });
    await group.destroy();  
    return { message: "Xóa nhóm thành công" };
}