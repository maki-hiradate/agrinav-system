package com.agriguide.model;

/**
 * 走行履歴データを保存するクラス
 * データベースのtrip_dataテーブルと対応しています
 */
public class TripData {
    // フィールド（データの項目）
    private int id;              // データのID
    private double speed;        // 速度 (km/h)
    private double distance;     // 距離 (m)
    private double latitude;     // 緯度
    private double longitude;    // 経度
    
    // コンストラクタ1: 空のデータを作る
    public TripData() {
    }
    
    // コンストラクタ2: 全ての値を設定する
    public TripData(int id, double speed, double distance, double latitude, double longitude) {
        this.id = id;
        this.speed = speed;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // コンストラクタ3: IDなしで作る（新規データ用）
    public TripData(double speed, double distance, double latitude, double longitude) {
        this.speed = speed;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // ゲッター：値を取得するメソッド
    public int getId() {
        return id;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public double getDistance() {
        return distance;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    // セッター：値を設定するメソッド
    public void setId(int id) {
        this.id = id;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    // デバッグ用：データを文字列で表示
    @Override
    public String toString() {
        return "TripData{id=" + id + ", speed=" + speed + ", distance=" + distance + 
               ", lat=" + latitude + ", lng=" + longitude + "}";
    }
}
