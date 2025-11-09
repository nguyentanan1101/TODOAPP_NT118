import {  GroupMember } from '../models/group_member.model.js';
import { PerformanceRecord } from '../models/performance_record.model.js';

export async function evaluateMemberService({ group_id, user_id, score, comment, created_by }) {
    if (!group_id || !user_id || score === undefined || !created_by) {
        throw { status: 400, message: "Cần nhập đầy đủ ID nhóm, ID người dùng, điểm số và ID người đánh giá" };
    }
    const evaluater = await GroupMember.findOne({ where: { group_id, user_id: created_by } });

    if(!evaluater) {
        throw { status: 403, message: "Người đánh giá không phải là thành viên của nhóm" };
    }

    if(evaluater.role !== 'Owner') {
        throw { status: 403, message: "Chỉ chủ nhóm mới có quyền đánh giá thành viên" };
    }

    const member = await GroupMember.findOne({ where: { group_id, user_id } });
    if(!member) {
        throw { status: 404, message: "Thành viên không tồn tại trong nhóm" };
    }

    const performancerecord = await PerformanceRecord.create({
        group_id,
        user_id,
        score,
        comment,
        created_by
    });
    return performancerecord;
}


export async function getMemberPerformancesService({ group_id, user_id }) {
    if (!group_id || !user_id) {
        throw { status: 400, message: "Cần nhập đầy đủ ID nhóm và ID người dùng" };
    }

    const performances = await PerformanceRecord.findAll({ where: { group_id, user_id } });
    return performances;
}
