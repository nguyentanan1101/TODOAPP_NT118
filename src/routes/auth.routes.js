import { Router } from "express";
import { authenticateJWT } from "../middleware/auth.middleware.js";
import { SignUp, SignIn, SignOut, ResetPassword, ForgotPassword } from "../controllers/auth.controller.js";   
const AuthRouter = Router();

AuthRouter.post("/Sign-up", SignUp);

AuthRouter.post("/Sign-in", SignIn);

AuthRouter.post("/Sign-out", authenticateJWT, SignOut);

AuthRouter.post("/forgot-password", ForgotPassword);

AuthRouter.post("/reset-password", ResetPassword);

AuthRouter.get ("/user-info", (req, res) => {res.send({ title: 'User Info' });});

export default AuthRouter;
