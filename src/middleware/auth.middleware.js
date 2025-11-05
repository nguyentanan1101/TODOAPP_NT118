import jwt from "jsonwebtoken";
import { JWT_SECRET } from "../config/env.js";

export const authenticateJWT = async (req, res, next) => {
  try {
    const accessToken = req.headers["authorization"]?.split(" ")[1];

    if (!accessToken) {
      return res.status(401).json({ message: "Access token is required" });
    }

    jwt.verify(accessToken, JWT_SECRET, (err, decoded) => {
      if (err) {
        if (err.name === "TokenExpiredError") {
          return res.status(401).json({
            message: "Access token has expired",
            code: "TOKEN_EXPIRED",
          });
        }
        return res.status(403).json({ message: "Invalid token" });
      }

      req.user = decoded;
      next();
    });
  } catch (err) {
    next(err);
  }
};
