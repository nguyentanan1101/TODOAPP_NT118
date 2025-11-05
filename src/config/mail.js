import nodemailer from 'nodemailer';
import { NODEMAILER_PASSWORD,NODEMAILER_USER } from "./env.js";

const transporter = nodemailer.createTransport({
    service: 'Gmail',
    auth: {
        user: NODEMAILER_USER,
        pass: NODEMAILER_PASSWORD
    },
});


export { transporter };