# Stage 1: Build với Maven
FROM maven:3.8.4-openjdk-11-slim AS builder
WORKDIR /app

# Vì Dockerfile nằm sâu, chúng ta phải đi ngược ra ngoài 4 cấp thư mục 
# (stg1 -> dockerfile -> ci -> devops -> root)
COPY ../../../../pom.xml .
# Chạy verify để tải tất cả dependencies và plugins cần thiết cho lifecycle.
RUN mvn -B verify clean --fail-never

COPY ../../../../src ./src
RUN mvn package -DskipTests -o

# Stage 2: Runtime với Tomcat (như doanh nghiệp bạn hay dùng)
FROM tomcat:10.1-jdk11-openjdk-slim
RUN rm -rf /usr/local/tomcat/webapps/*

# Tạo thư mục cấu hình để có thể mount file config.xml vào
RUN mkdir -p /app/config
ENV CONFIG_FILE_PATH=/app/config/config.xml

# Copy file .war vừa build xong vào Tomcat
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]