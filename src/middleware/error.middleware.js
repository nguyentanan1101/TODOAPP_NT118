export function errorHandler(err, req, res, next) {
  console.error("ERROR:", err);

  const statusCode = err.status || 500;

  return res.status(statusCode).json({
    status: statusCode,
    message: err.message || "Lỗi hệ thống"
  });
}
