package com.agriguide.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * データベース接続を管理するクラス
 * このクラスを使ってデータベースに接続します
 */
public class DatabaseUtil {
    
    // データベースの接続情報（あなたの環境に合わせて変更してください）
    private static final String DB_URL = "jdbc:mysql://localhost:3306/agriguide_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root1234";
    
    /**
     * データベースに接続する
     * @return Connection データベースへの接続
     */
    public static Connection getConnection() throws SQLException {
        try {
            // MySQLドライバーを読み込む
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // データベースに接続
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ データベースに接続しました");
            return conn;
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQLドライバーが見つかりません");
            throw new SQLException("ドライバーエラー", e);
        } catch (SQLException e) {
            System.out.println("❌ データベース接続エラー: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * 接続を安全に閉じる
     * @param conn 閉じる接続
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("✅ データベース接続を閉じました");
            } catch (SQLException e) {
                System.out.println("⚠️ 接続を閉じる際にエラー: " + e.getMessage());
            }
        }
    }
}
