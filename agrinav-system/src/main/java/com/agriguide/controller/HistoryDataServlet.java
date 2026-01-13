package com.agriguide.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.agriguide.model.TripData;
import com.agriguide.service.TripDataService;
import com.agriguide.util.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 走行履歴データAPIのコントローラー
 * /api/history-data にアクセスしたときの処理を担当します
 */
@WebServlet("/agrinav-system/api/history-data")
public class HistoryDataServlet extends HttpServlet {
    
    private TripDataService service;
    
    /**
     * Servlet起動時に1回だけ実行される
     */
    @Override
    public void init() throws ServletException {
        super.init();
        this.service = new TripDataService();
        System.out.println("✅ HistoryDataServlet を起動しました");
    }
    
    /**
     * GETリクエストの処理
     * 走行履歴データを返す
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CORS設定（別のドメインからアクセスを許可）
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        // レスポンスの設定
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // limitパラメータを取得（デフォルト10件）
            String limitParam = request.getParameter("limit");
            int limit = 10;
            
            if (limitParam != null) {
                try {
                    limit = Integer.parseInt(limitParam);
                } catch (NumberFormatException e) {
                    // パラメータが数値でない場合はデフォルト値を使用
                }
            }
            
            // データを取得
            List<TripData> dataList = service.getLatestTripData(limit);
            
            // JSON配列に変換
            String jsonResponse = JsonUtil.toJsonArray(dataList);
            
            // レスポンスを書き込む
            PrintWriter out = response.getWriter();
            out.print(jsonResponse);
            
            System.out.println("✅ 履歴データを返しました: " + dataList.size() + "件");
            
        } catch (Exception e) {
            System.out.println("❌ エラー: " + e.getMessage());
            response.setStatus(500);  // エラーステータス
            PrintWriter out = response.getWriter();
            out.print(JsonUtil.toErrorJson("サーバーエラー"));
        }
    }
    
    /**
     * POSTリクエストの処理
     * 新しい走行データを保存
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CORS設定
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // パラメータを取得
            String speedParam = request.getParameter("speed");
            String distanceParam = request.getParameter("distance");
            String latitudeParam = request.getParameter("latitude");
            String longitudeParam = request.getParameter("longitude");
            
            // パラメータが無い場合はエラー
            if (speedParam == null || distanceParam == null || 
                latitudeParam == null || longitudeParam == null) {
                response.setStatus(400);  // Bad Request
                PrintWriter out = response.getWriter();
                out.print(JsonUtil.toErrorJson("全てのパラメータが必要です"));
                return;
            }
            
            // 数値に変換
            double speed = Double.parseDouble(speedParam);
            double distance = Double.parseDouble(distanceParam);
            double latitude = Double.parseDouble(latitudeParam);
            double longitude = Double.parseDouble(longitudeParam);
            
            // データを保存
            int id = service.saveTripData(speed, distance, latitude, longitude);
            
            // レスポンスを返す
            PrintWriter out = response.getWriter();
            out.print("{\"success\":true,\"id\":" + id + "}");
            
        } catch (NumberFormatException e) {
            response.setStatus(400);
            PrintWriter out = response.getWriter();
            out.print(JsonUtil.toErrorJson("数値が不正です"));
        } catch (Exception e) {
            response.setStatus(500);
            PrintWriter out = response.getWriter();
            out.print(JsonUtil.toErrorJson("サーバーエラー"));
        }
    }
}
