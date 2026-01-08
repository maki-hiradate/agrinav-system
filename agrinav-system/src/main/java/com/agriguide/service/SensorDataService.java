package com.agriguide.service;

import com.agriguide.model.SensorData;
import com.agriguide.repository.SensorDataRepository;

/**
 * センサーデータの処理を行うクラス
 * データの検証や、Repositoryへの橋渡しをします
 */
public class SensorDataService {
    
    private SensorDataRepository repository;
    
    // コンストラクタ
    public SensorDataService() {
        this.repository = new SensorDataRepository();
    }
    
    /**
     * 最新のセンサーデータを取得
     * @return SensorData
     */
    public SensorData getLatestSensorData() {
        SensorData data = repository.findLatest();
        System.out.println("📊 最新データ: " + data);
        return data;
    }
    
    /**
     * 新しいセンサーデータを保存
     * @param speed 速度
     * @param distance 距離
     * @return 保存されたデータのID
     */
    public int saveSensorData(double speed, double distance) {
        // データの検証
        if (speed < 0 || speed > 100) {
            System.out.println("❌ 速度の値が不正です: " + speed);
            return 0;
        }
        
        if (distance < 0 || distance > 100000) {
            System.out.println("❌ 距離の値が不正です: " + distance);
            return 0;
        }
        
        // データを保存
        SensorData data = new SensorData(speed, distance);
        return repository.save(data);
    }
}
