export const generateResetPasswordEmail = (email, resetCode, expiryTime) => {
  const currentYear = new Date().getFullYear();

  return `
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Đặt lại mật khẩu</title>
  <style>
    body { font-family: Arial, sans-serif; color:#333; margin:0; padding:0; }
    .container { max-width:600px; margin:0 auto; padding:20px; }
    .header { background:#f8f9fa; padding:20px; text-align:center; }
    .content { padding:20px; }
    .code-box {
      display:inline-block;
      background:#f1f1f1;
      border-radius:6px;
      padding:16px 22px;
      font-size:28px;
      letter-spacing:4px;
      font-weight:700;
      font-family: 'Courier New', Courier, monospace;
      color:#111;
      margin:16px 0;
    }
    .note { font-size:14px; color:#666; margin-top:8px; }
    .footer { text-align:center; padding:20px; font-size:12px; color:#666; }
    @media (max-width:480px){
      .code-box { font-size:22px; padding:12px 18px; }
    }
  </style>
</head>
<body>
  <div class="container">
    <div class="header">
      <h2>Yêu cầu đặt lại mật khẩu</h2>
    </div>

    <div class="content">
      <p>Xin chào ${email},</p>

      <p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Vui lòng nhập mã sau vào trang đặt lại mật khẩu của ứng dụng để xác thực và thiết lập mật khẩu mới.</p>

      <div style="text-align:center;">
        <div class="code-box">${resetCode}</div>
      </div>

      <p class="note">Mã này sẽ hết hạn sau ${expiryTime} phút. Không chia sẻ mã này với bất kỳ ai.</p>

      <p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này. Mọi hoạt động đáng ngờ, vui lòng liên hệ bộ phận hỗ trợ.</p>
    </div>

    <div class="footer">
      <p>Đây là email tự động. Vui lòng không trả lời thư này.</p>
      <p>&copy; ${currentYear} Nhóm 04 Lập trình mobile.</p>
    </div>
  </div>
</body>
</html>
  `;
};