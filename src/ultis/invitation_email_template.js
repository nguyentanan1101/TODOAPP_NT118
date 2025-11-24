export const generateInviteEmail = ({
  receiverEmail,
  inviterName,
  targetType,      
  targetName,
  role,
  acceptUrl
}) => {
  const currentYear = new Date().getFullYear();

  return `
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Lời mời tham gia ${targetType}</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <style>
    body { background:#f5f6fa; font-family:Arial; margin:0; padding:0; }
    .container { max-width:600px; margin:40px auto; background:#fff; border-radius:12px;
      box-shadow:0 4px 12px rgba(0,0,0,0.08); overflow:hidden; }
    .header { background:#4a90e2; padding:25px; text-align:center; color:#fff; }
    .content { padding:25px; line-height:1.6; }
    .highlight-box {
      background:#f0f3f7; border-left:4px solid #4a90e2;
      padding:15px; margin:20px 0; border-radius:6px;
    }
    .btn { display:inline-block; background:#4a90e2; color:#fff;
      padding:12px 22px; border-radius:6px; text-decoration:none; }
    .footer { text-align:center; padding:20px; color:#666; font-size:13px; }
  </style>
</head>
<body>
  <div class="container">
    <div class="header">
      <h2>Lời mời tham gia ${targetType}</h2>
    </div>
    <div class="content">
      <p>Xin chào <b>${receiverEmail}</b>,</p>

      <p><b>${inviterName}</b> đã mời bạn tham gia vào ${targetType.toLowerCase()}:</p>

      <div class="highlight-box">
        <b>${targetName}</b><br>
        Vai trò: <b>${role}</b>
      </div>

      <p>Nhấn vào nút dưới đây để chấp nhận lời mời:</p>

      <p style="text-align:center">
        <a href="${acceptUrl}" class="btn">Chấp nhận lời mời</a>
      </p>

      <p>Nếu bạn không nhận ra lời mời này, hãy bỏ qua email.</p>
    </div>
    <div class="footer">
      <p>Email tự động – vui lòng không trả lời.</p>
      <p>&copy; ${currentYear} Team 04 Mobile.</p>
    </div>
  </div>
</body>
</html>
  `;
};
