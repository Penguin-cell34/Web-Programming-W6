# =========================================================
# Stage 1: Build Maven project
# =========================================================

FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml trước để Docker cache dependency
COPY pom.xml .

RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build WAR
RUN mvn clean package -DskipTests


# =========================================================
# Stage 2: Run with Tomcat 11
# =========================================================

FROM tomcat:11.0-jdk17-temurin

# Xóa webapp mặc định của Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR thành ROOT.war
COPY --from=build /app/target/*.war \
    /usr/local/tomcat/webapps/ROOT.war

# Render sử dụng biến PORT
ENV PORT=8080

EXPOSE 8080

# Chạy Tomcat
CMD ["catalina.sh", "run"]
