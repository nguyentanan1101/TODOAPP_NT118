import { evaluateMember, getMemberPerformances } from '../controllers/performance.controller.js';
import { authenticateJWT } from '../middleware/auth.middleware.js';
import { Router } from 'express';

const PerformanceRouter = Router();

PerformanceRouter.post('/evaluate', authenticateJWT, evaluateMember);

PerformanceRouter.get('/member/:group_id/performances', authenticateJWT, getMemberPerformances);

export default PerformanceRouter;