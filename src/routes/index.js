import Router from 'express';
import AuthRouter from './auth.routes.js';
import TaskRouter from './task.routes.js';

const router = Router();

router.use('/auth', AuthRouter);
router.use('/tasks', TaskRouter);


export default router;