FROM node:22.19.0-alpine

# Set working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install --production

# Copy source code
COPY . .

# Expose port (adjust if needed)
EXPOSE 3000

# Start the application
CMD ["npm", "start"]

