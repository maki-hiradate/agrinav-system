package com.agriguide.util;

import com.agriguide.model.TripData;
import java.util.List;

/**
 * JavaオブジェクトをJSON文字列に変換するクラス
 * フロントエンドにデータを送るときに使います
 */
public class JsonUtil {
    
    /**
     * TripDataをJSON文字列に変換
     * 例: {"id":1,"speed":8.5,"distance":150.3,"latitude":36.5,"longitude":138.5}
     */
    public static String toJson(TripData data) {
        if (data == null) {
            return "{}";
        }
        
        // JSON形式の文字列を作成
        return String.format(
            "{\"id\":%d,\"speed\":%.1f,\"distance\":%.1f,\"latitude\":%.6f,\"longitude\":%.6f}",
            data.getId(),
            data.getSpeed(),
            data.getDistance(),
            data.getLatitude(),
            data.getLongitude()
        );
    }
    
    /**
     * TripDataのリストをJSON配列に変換
     * 例: [{"id":1,...},{"id":2,...}]
     */
    public static String toJsonArray(List<TripData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return "[]";
        }
        
        // JSON配列の文字列を作成
        StringBuilder json = new StringBuilder("[");
        
        for (int i = 0; i < dataList.size(); i++) {
            if (i > 0) {
                json.append(",");  // 2個目以降はカンマを追加
            }
            json.append(toJson(dataList.get(i)));
        }
        
        json.append("]");
        return json.toString();
    }
    
    /**
     * エラーメッセージをJSON形式で返す
     * 例: {"error":"データが見つかりません"}
     */
    public static String toErrorJson(String errorMessage) {
        return "{\"error\":\"" + errorMessage + "\"}";
    }
}
