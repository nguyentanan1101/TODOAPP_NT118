import Router from 'express';
import AuthRouter from './auth.routes.js';
import TaskRouter from './task.routes.js';
import GroupRouter from './group.routes.js';
import ProjectRouter from './project.routes.js';
import PerformanceRouter from './performance.routes.js';

const router = Router();

router.use('/auth', AuthRouter);


router.use('/tasks', TaskRouter);


router.use('/projects', ProjectRouter);


router.use('/groups', GroupRouter);


router.use('/performance', PerformanceRouter);


export default router;