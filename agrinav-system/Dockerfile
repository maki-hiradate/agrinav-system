# ビルドステージ
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# pom.xmlとソースコードをコピー
COPY pom.xml .
COPY src ./src

# Mavenでビルド（テストはスキップ）
RUN mvn clean package -DskipTests

# 実行ステージ
FROM tomcat:10.1-jdk17

WORKDIR /usr/local/tomcat

# デフォルトのwebappsを削除
RUN rm -rf webapps/*

# ビルドステージからWARファイルをコピー
COPY --from=build /app/target/*.war webapps/ROOT.war

# ポート公開
EXPOSE 8080

# Tomcat起動
CMD ["catalina.sh", "run"]