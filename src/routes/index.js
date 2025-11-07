import Router from 'express';
import AuthRouter from './auth.routes.js';
import TaskRouter from './task.routes.js';
import GroupRouter from './group.routes.js';

const router = Router();

router.use('/auth', AuthRouter);
router.use('/tasks', TaskRouter);
router.use('/groups', GroupRouter);


export default router;