import { User } from "../models/auth.model.js";
import { RefreshToken } from "../models/token.model.js";
import jwt from "jsonwebtoken";
import { JWT_SECRET } from "../config/env.js";
import bcrypt from "bcryptjs";
import { generateResetPasswordEmail } from "../ultis/email-template.js";
import { transporter } from "../config/mail.js";
import { Op } from "sequelize";

function generateAccessToken(user) {
  return jwt.sign(
    { id: user.user_id, email: user.email },
    JWT_SECRET,
    { expiresIn: "1h" }
  );
}

function generateRefreshToken(user) {
  return jwt.sign(
    { id: user.user_id },
    JWT_SECRET,
    { expiresIn: "7d" }
  );
}

export async function signUpService({ email, phone_number, username, address, birthday, password }) {
  if ((!email && !phone_number) || !password) {
    throw { status: 400, message: "Cần nhập Email hoặc số điện thoại và mật khẩu" };
  }

  
  if (email) {
    const existingEmail = await User.findOne({ where: { email } });
    if (existingEmail) throw { status: 400, message: "Email đã tồn tại" };
  }

  
  if (phone_number) {
    const existingPhone = await User.findOne({ where: { phone_number } });
    if (existingPhone) throw { status: 400, message: "Số điện thoại đã tồn tại" };
  }

  const hashedPassword = await bcrypt.hash(password, 10);
  console.log("Data to insert:", {
    email,
    phone_number,
    username,
    address,
    birthday,
    password: '[hashed]'
  });
  return await User.create({ email, phone_number, username, address, birthday, password: hashedPassword });
}

export async function signInService({ email, phone_number, password }) {
  if (!password || (!email && !phone_number)) {
    throw { status: 400, message: "Cần nhập Email hoặc Số điện thoại và Mật khẩu" };
  }

  const user = email ? await User.findOne({ where: { email } }) : await User.findOne({ where: { phone_number } });  

  if (!user) throw { status: 401, message: "Email hoặc số điện thoại không hợp lệ" };

  const validPassword = await bcrypt.compare(password, user.password);
  if (!validPassword) throw { status: 401, message: "Mật khẩu không hợp lệ" };

  const accessToken = generateAccessToken(user);
  const refreshToken = generateRefreshToken(user);

  await RefreshToken.create({
    token: refreshToken,
    user_id: user.user_id,
    expires_at: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)
  });

  return { user, accessToken, refreshToken };
}


export async function getUserByIdService(user_id) {
  const user = await User.findByPk(user_id, {
    attributes: { exclude: ['password', 'reset_token', 'reset_expires'] }
  });
  if (!user) {
    throw { status: 404, message: "Người dùng không tồn tại" };
  }
  return user;
}


export async function updateUserProfileService(user_id, {email, phone_number, username, address, birthday }) {
  const user = await User.findByPk(user_id);
  if (!user) {
    throw { status: 404, message: "Người dùng không tồn tại" };
  } 
  if (email) user.email = email;
  if (phone_number) user.phone_number = phone_number;
  if (username) user.username = username;
  if (address) user.address = address;
  if (birthday) user.birthday = birthday;
  await user.save();
  return user;
}

export async function getUserIdByEmailService(email) {
  const user = await User.findOne({ where: { email } });
  if (!user) {
    throw { status: 404, message: "Người dùng không tồn tại" };
  } 
  return user.user_id;
}

export async function refreshTokenService(token) {
  if (!token) {
    throw { status: 400, message: "Cần cung cấp refresh token" };
  }

  const refreshTokenDoc = await RefreshToken.findOne({
    where: {
      token,
      is_revoked: false,
      expires_at: { [Op.gt]: new Date() }
    }
  });

  if (!refreshTokenDoc) {
    throw { status: 401, message: "Refresh token không hợp lệ hoặc đã hết hạn" };
  }

  const user = await User.findByPk(refreshTokenDoc.user_id);
  const newAccessToken = generateAccessToken(user);

  return { accessToken: newAccessToken };
}

export async function signOutService(refreshToken) {
  if (!refreshToken) {
    throw { status: 400, message: "Cần cung cấp refresh token" };
  }

  await RefreshToken.update(
    { is_revoked: true },
    { where: { token: refreshToken } }
  );

  return { message: "Đăng xuất thành công" };
}

export async function forgotPasswordService(email) {
  const user = await User.findOne({ where: { email } });
  if (!user) throw { status: 400, message: "Không tìm thấy tài khoản với email này" };

  const resetCode = Math.floor(1000 + Math.random() * 9000).toString();

  user.reset_token = resetCode;
  user.reset_expires = Date.now() + 15 * 60 * 1000;
  await user.save();

  const html = generateResetPasswordEmail(user.email, resetCode, 15);

  await transporter.sendMail({
    from: '"Your App" <no-reply@yourapp.com>',
    to: user.email,
    subject: "Mã đặt lại mật khẩu",
    html
  });

  return { message: "Reset code sent" };
}

export async function resetPasswordService(token, newPassword) {
  const user = await User.findOne({ where: { reset_token: token } });
  if (!user) throw { status: 400, message: "Mã đặt lại mật khẩu không hợp lệ hoặc đã hết hạn" };

  if (user.reset_expires < Date.now()) {
    throw { status: 400, message: "Mã đặt lại mật khẩu đã hết hạn" };
  }

  user.password = await bcrypt.hash(newPassword, 10);
  user.reset_token = null;
  user.reset_expires = null;
  await user.save();

  return { message: "Mật khẩu đã được đặt lại thành công" };
}
