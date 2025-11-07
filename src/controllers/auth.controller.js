import { signUpService, signInService, signOutService,getUserByIdService ,resetPasswordService, forgotPasswordService, updateUserProfileService } from "../services/authservices.js";

export async function signUp(req, res, next) {
  try {
    const newUser = await signUpService(req.body);
    return res.status(201).json({
      user_id: newUser.user_id,
      email: newUser.email,
      phone_number: newUser.phone_number,
      address: newUser.address,
      birthday: newUser.birthday,
      message: "Đăng ký thành công"
    });
  } catch (err) {
    if (err && err.status) return res.status(err.status).json({ message: err.message });
    next(err);
  }
}

export async function signIn(req, res, next) {
  try {
    const { user, accessToken, refreshToken } = await signInService(req.body);
    return res.status(200).json({
      message: "Đăng nhập thành công",
      accessToken,
      refreshToken,
      user: { user_id: user.user_id, email: user.email, phone_number: user.phone_number, address: user.address, birthday: user.birthday }
    });
  } catch (err) {
    if (err && err.status) return res.status(err.status).json({ message: err.message });
    next(err);
  }
}

export async function getUserById(req, res, next) {
  const user_id = req.user.id;
  try {
    const user = await getUserByIdService(user_id);
    return res.status(200).json({
      email: user.email,
      phone_number: user.phone_number,
      address: user.address,
      birthday: user.birthday
    });
  } catch (err) { 
    if (err && err.status) return res.status(err.status).json({ message: err.message });
    next(err);
  }
}

export async function updateUserProfile(req, res, next) {
  const user_id = req.user.id;
  try {
    const updatedUser = await updateUserProfileService(user_id, req.body);
    return res.status(200).json({
      email: updatedUser.email,
      phone_number: updatedUser.phone_number,
      address: updatedUser.address,
      birthday: updatedUser.birthday,
      message: "Cập nhật thông tin người dùng thành công"
    });
  } catch (err) { 
    if (err && err.status) return res.status(err.status).json({ message: err.message });
    next(err);
  }
}
    

export async function forgotPassword(req, res, next) {
  try {
    const { email } = req.body;
    await forgotPasswordService(email);
    return res.status(200).json({ message: "Email đặt lại mật khẩu đã được gửi" });
  } catch (err) {
    if (err && err.status) return res.status(err.status).json({ message: err.message });
    next(err);
  }
}

export async function resetPassword(req, res, next) {
  try {
    const { token, newPassword } = req.body;
    const result = await resetPasswordService(token, newPassword);
    return res.status(200).json({ message: result.message });
  } catch (err) {
    if (err && err.status) return res.status(err.status).json({ message: err.message });
    next(err);
  }
}

export async function signOut(req, res, next) {
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