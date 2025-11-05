import { config } from "dotenv";
import path from "path";

config({
  path: path.resolve(process.cwd(), `.env.${process.env.NODE_ENV || "Development"}.local`),
});
console.log("✅ Loaded ENV from:", `.env.${process.env.NODE_ENV || "Development"}.local`);
console.log("DB_NAME =", process.env.DB_NAME);
export const PORT = process.env.PORT;
export const DB_HOST = process.env.DB_HOST;
export const DB_USER = process.env.DB_USER;
export const DB_PASSWORD = process.env.DB_PASSWORD;
export const DB_NAME = process.env.DB_NAME;
export const DB_PORT = process.env.DB_PORT;
export const JWT_SECRET = process.env.JWT_SECRET;
export const JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN;
export const NODEMAILER_USER = process.env.NODEMAILER_USER;
export const NODEMAILER_PASSWORD = process.env.NODEMAILER_PASSWORD;