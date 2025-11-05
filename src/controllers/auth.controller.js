import { signUpService, signInService, signOutService, resetPasswordService, forgotPasswordService } from "../services/authservices.js";

export async function SignUp(req, res, next) {
  try {
    const newUser = await signUpService(req.body);
    return res.status(201).json({
      user_id: newUser.user_id,
      email: newUser.email,
      phone_number: newUser.phone_number
    });
  } catch (err) {
    if (err && err.status) return res.status(err.status).json({ message: err.message });
    next(err);
  }
}

export async function SignIn(req, res, next) {
  try {
    const { user, accessToken, refreshToken } = await signInService(req.body);
    return res.status(200).json({
      message: "Đăng nhập thành công",
      accessToken,
      refreshToken,
      user: { user_id: user.user_id, email: user.email, phone_number: user.phone_number }
    });
  } catch (err) {
    if (err && err.status) return res.status(err.status).json({ message: err.message });
    next(err);
  }
}

export async function ForgotPassword(req, res, next) {
  try {
    const { email } = req.body;
    await forgotPasswordService(email);
    return res.status(200).json({ message: "Email đặt lại mật khẩu đã được gửi" });
  } catch (err) {
    if (err && err.status) return res.status(err.status).json({ message: err.message });
    next(err);
  }
}

export async function ResetPassword(req, res, next) {
  try {
    const { token, newPassword } = req.body;
    const result = await resetPasswordService(token, newPassword);
    return res.status(200).json({ message: result.message });
  } catch (err) {
    if (err && err.status) return res.status(err.status).json({ message: err.message });
    next(err);
  }
}

export async function SignOut(req, res, next) {
  try {
    const refreshToken = req.body?.refreshToken || req.headers['x-refresh-token'] || (req.cookies && req.cookies.refreshToken);
    if (!refreshToken) {
      return res.status(400).json({ message: "Cần cung cấp refresh token" });
    }

    const result = await signOutService(refreshToken);
    return res.status(200).json({ message: result.message });
  } catch (err) {
    if (err && err.status) return res.status(err.status).json({ message: err.message });
    next(err);
  }
}