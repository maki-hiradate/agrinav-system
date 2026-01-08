package com.agriguide.repository;

import com.agriguide.model.TripData;
import com.agriguide.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * trip_dataテーブルからデータを取得・保存するクラス
 * 走行履歴データのデータベース操作を担当します
 */
public class TripDataRepository {
    
    /**
     * 最新のN件の走行データを取得
     * @param limit 取得する件数
     * @return TripDataのリスト
     */
    public List<TripData> findLatest(int limit) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<TripData> dataList = new ArrayList<>();
        
        try {
            // データベースに接続
            conn = DatabaseUtil.getConnection();
            
            // SQL文を準備（最新のN件を取得）
            String sql = "SELECT id, speed, distance, latitude, longitude " +
                        "FROM trip_data ORDER BY id DESC LIMIT ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            
            // SQL実行
            rs = stmt.executeQuery();
            
            // 結果を1件ずつTripDataに変換してリストに追加
            while (rs.next()) {
                TripData data = new TripData();
                data.setId(rs.getInt("id"));
                data.setSpeed(rs.getDouble("speed"));
                data.setDistance(rs.getDouble("distance"));
                data.setLatitude(rs.getDouble("latitude"));
                data.setLongitude(rs.getDouble("longitude"));
                dataList.add(data);
            }
            
            System.out.println("✅ " + dataList.size() + "件のデータを取得しました");
            return dataList;
            
        } catch (SQLException e) {
            System.out.println("❌ データ取得エラー: " + e.getMessage());
            return dataList;  // 空のリストを返す
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
     * 新しい走行データを保存
     * @param data 保存するTripData
     * @return 保存されたデータのID
     */
    public int save(TripData data) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // データベースに接続
            conn = DatabaseUtil.getConnection();
            
            // SQL文を準備（データ挿入）
            String sql = "INSERT INTO trip_data (speed, distance, latitude, longitude) " +
                        "VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            // 値を設定
            stmt.setDouble(1, data.getSpeed());
            stmt.setDouble(2, data.getDistance());
            stmt.setDouble(3, data.getLatitude());
            stmt.setDouble(4, data.getLongitude());
            
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
