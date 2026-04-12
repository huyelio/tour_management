# =============================================
# Stage 1: Build với Maven
# =============================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy file pom.xml và tải dependencies trước (tận dụng Docker cache layer)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy source code và build
COPY src ./src
RUN ./mvnw package -DskipTests -B

# =============================================
# Stage 2: Runtime image nhỏ gọn
# =============================================
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Tạo user non-root để bảo mật
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy JAR từ stage build
COPY --from=builder /app/target/*.jar app.jar

# Đổi ownership
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

# JVM options tối ưu cho container
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
