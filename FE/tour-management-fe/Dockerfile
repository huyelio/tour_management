# =============================================
# Stage 1: Build React app với Node
# =============================================
FROM node:22-alpine AS builder

WORKDIR /app

# Copy package files trước (tận dụng Docker cache layer)
COPY package*.json ./
RUN npm ci --prefer-offline

# Copy toàn bộ source và build
COPY . .
RUN npm run build

# =============================================
# Stage 2: Serve bằng Nginx nhỏ gọn
# =============================================
FROM nginx:alpine AS runtime

# Xóa config mặc định của nginx
RUN rm /etc/nginx/conf.d/default.conf

# Copy nginx config tùy chỉnh
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy build output từ stage 1
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
