import { evaluateMemberService, getMemberPerformancesService } from '../services/performanceservices.js';
import { getUserIdByEmailService } from "../services/authservices.js";

export async function evaluateMember(req, res, next) {
    try {
        const { group_id, email, score, comment } = req.body;
        const user_id = await getUserIdByEmailService(email);
        const created_by = req.user.id;
        const performanceRecord = await evaluateMemberService({ group_id, user_id, score, comment, created_by });
        return res.status(201).json({
            message: "Đánh giá thành viên thành công",
            performanceRecord
        });
    }
    catch (err) {
        if (err && err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}


export async function getMemberPerformances(req, res, next) {
    try {
        const group_id = req.params.group_id;
        const user_id = req.user.id;
        const performances = await getMemberPerformancesService({ group_id, user_id });
        return res.status(200).json({ performances });
    } catch (err) {
        if (err && err.status) return res.status(err.status).json({ message: err.message });
        next(err);
    }
}