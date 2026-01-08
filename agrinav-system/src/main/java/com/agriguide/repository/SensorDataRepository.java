package com.agriguide.repository;

import com.agriguide.model.SensorData;
import com.agriguide.util.DatabaseUtil;
import java.sql.*;

/**
 * sensor_dataテーブルからデータを取得・保存するクラス
 * データベースとのやり取りを担当します
 */
public class SensorDataRepository {
    
    /**
     * 最新のセンサーデータを1件取得
     * @return 最新のSensorData
     */
    public SensorData findLatest() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // データベースに接続
            conn = DatabaseUtil.getConnection();
            
            // SQL文を準備（最新の1件を取得）
            String sql = "SELECT id, speed, distance FROM sensor_data ORDER BY id DESC LIMIT 1";
            stmt = conn.prepareStatement(sql);
            
            // SQL実行
            rs = stmt.executeQuery();
            
            // 結果があればSensorDataに変換
            if (rs.next()) {
                SensorData data = new SensorData();
                data.setId(rs.getInt("id"));
                data.setSpeed(rs.getDouble("speed"));
                data.setDistance(rs.getDouble("distance"));
                return data;
            }
            
            // データがない場合はデフォルト値を返す
            return new SensorData(0, 0.0, 0.0);
            
        } catch (SQLException e) {
            System.out.println("❌ データ取得エラー: " + e.getMessage());
            return new SensorData(0, 0.0, 0.0);
        } finally {
            // リソースを閉じる
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("⚠️ リソースクローズエラー");
            }
        }
    }
    
    /**
     * 新しいセンサーデータを保存
     * @param data 保存するSensorData
     * @return 保存されたデータのID
     */
    public int save(SensorData data) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // データベースに接続
            conn = DatabaseUtil.getConnection();
            
            // SQL文を準備（データ挿入）
            String sql = "INSERT INTO sensor_data (speed, distance) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            // 値を設定
            stmt.setDouble(1, data.getSpeed());
            stmt.setDouble(2, data.getDistance());
            
            // SQL実行
            stmt.executeUpdate();
            
            // 自動生成されたIDを取得
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                System.out.println("✅ データを保存しました ID: " + id);
                return id;
            }
            
            return 0;
            
        } catch (SQLException e) {
            System.out.println("❌ データ保存エラー: " + e.getMessage());
            return 0;
        } finally {
            // リソースを閉じる
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("⚠️ リソースクローズエラー");
            }
        }
    }
}
