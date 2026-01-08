package com.agriguide.service;

import com.agriguide.model.TripData;
import com.agriguide.repository.TripDataRepository;
import java.util.List;

/**
 * 走行データの処理を行うクラス
 * データの検証や、Repositoryへの橋渡しをします
 */
public class TripDataService {
    
    private TripDataRepository repository;
    
    // コンストラクタ
    public TripDataService() {
        this.repository = new TripDataRepository();
    }
    
    /**
     * 最新のN件の走行データを取得
     * @param limit 取得件数
     * @return TripDataのリスト
     */
    public List<TripData> getLatestTripData(int limit) {
        // limitの検証（1〜100件まで）
        if (limit < 1) {
            limit = 10;  // デフォルト10件
        }
        if (limit > 100) {
            limit = 100;  // 最大100件
        }
        
        List<TripData> dataList = repository.findLatest(limit);
        System.out.println("📊 走行データ: " + dataList.size() + "件取得");
        return dataList;
    }
    
    /**
     * 新しい走行データを保存
     * @param speed 速度
     * @param distance 距離
     * @param latitude 緯度
     * @param longitude 経度
     * @return 保存されたデータのID
     */
    public int saveTripData(double speed, double distance, double latitude, double longitude) {
        // データの検証
        if (speed < 0 || speed > 100) {
            System.out.println("❌ 速度の値が不正です: " + speed);
            return 0;
        }
        
        if (distance < 0 || distance > 100000) {
            System.out.println("❌ 距離の値が不正です: " + distance);
            return 0;
        }
        
        if (latitude < -90 || latitude > 90) {
            System.out.println("❌ 緯度の値が不正です: " + latitude);
            return 0;
        }
        
        if (longitude < -180 || longitude > 180) {
            System.out.println("❌ 経度の値が不正です: " + longitude);
            return 0;
        }
        
        // データを保存
        TripData data = new TripData(speed, distance, latitude, longitude);
        return repository.save(data);
    }
}
