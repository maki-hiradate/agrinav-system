package com.agriguide.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseUtil {
    
    // 環境変数からDATABASE_URLを取得（Render用）
    private static final String DATABASE_URL = System.getenv("DATABASE_URL");
    
    // ローカル開発用のMySQL設定
    private static final String LOCAL_URL = "jdbc:mysql://localhost:3306/agrinav_db";
    private static final String LOCAL_USER = "root";
    private static final String LOCAL_PASSWORD = "root1234"; // ★ここを実際のパスワードに変更
    
    static {
        try {
            // PostgreSQLドライバーをロード（Render用）
            Class.forName("org.postgresql.Driver");
            // MySQLドライバーをロード（ローカル用）
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database driver not found", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        // DATABASE_URL環境変数が設定されている場合（Render環境）
        if (DATABASE_URL != null && !DATABASE_URL.isEmpty()) {
            System.out.println("Connecting to PostgreSQL (Render)...");
            // DATABASE_URLを JDBC形式に変換
            String jdbcUrl = DATABASE_URL;
            if (jdbcUrl.startsWith("postgresql://")) {
                jdbcUrl = jdbcUrl.replace("postgresql://", "jdbc:postgresql://");
            }
            return DriverManager.getConnection(jdbcUrl);
        } else {
            // ローカル環境（MySQL）
            System.out.println("Connecting to MySQL (Local)...");
            return DriverManager.getConnection(LOCAL_URL, LOCAL_USER, LOCAL_PASSWORD);
        }
    }
    
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}