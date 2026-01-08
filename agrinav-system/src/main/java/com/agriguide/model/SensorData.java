package com.agriguide.model;

/**
 * センサーデータを保存するクラス
 * データベースのsensor_dataテーブルと対応しています
 */
public class SensorData {
    // フィールド（データの項目）
    private int id;              // データのID
    private double speed;        // 速度 (km/h)
    private double distance;     // 距離 (m)
    
    // コンストラクタ1: 空のデータを作る
    public SensorData() {
    }
    
    // コンストラクタ2: 全ての値を設定する
    public SensorData(int id, double speed, double distance) {
        this.id = id;
        this.speed = speed;
        this.distance = distance;
    }
    
    // コンストラクタ3: IDなしで作る（新規データ用）
    public SensorData(double speed, double distance) {
        this.speed = speed;
        this.distance = distance;
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
    
    // デバッグ用：データを文字列で表示
    @Override
    public String toString() {
        return "SensorData{id=" + id + ", speed=" + speed + ", distance=" + distance + "}";
    }
}
