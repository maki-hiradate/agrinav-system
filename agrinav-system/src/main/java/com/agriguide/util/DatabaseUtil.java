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
    private static final String LOCAL_PASSWORD = "root1234";
    
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
            
            try {
                // RenderのDATABASE_URLをパース
                // 形式: postgresql://user:password@host:port/database
                String url = DATABASE_URL;
                if (url.startsWith("postgresql://")) {
                    url = url.substring("postgresql://".length());
                }
                
                // user:password@host:port/database を分解
                String userInfo = url.substring(0, url.indexOf("@"));
                String hostAndDb = url.substring(url.indexOf("@") + 1);
                
                String user = userInfo.substring(0, userInfo.indexOf(":"));
                String password = userInfo.substring(userInfo.indexOf(":") + 1);
                
                String jdbcUrl = "jdbc:postgresql://" + hostAndDb;
                
                System.out.println("JDBC URL: " + jdbcUrl);
                System.out.println("User: " + user);
                
                return DriverManager.getConnection(jdbcUrl, user, password);
                
            } catch (Exception e) {
                System.out.println("URL parse error: " + e.getMessage());
                throw new SQLException("Failed to parse DATABASE_URL", e);
            }
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