package com.agriguide.controller;

import com.agriguide.model.SensorData;
import com.agriguide.service.SensorDataService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * センサーデータAPIのコントローラー
 * /api/sensor-data にアクセスしたときの処理を担当します
 */
@WebServlet("/api/sensor-data")
public class SensorDataServlet extends HttpServlet {
    
    private SensorDataService service;
    
    /**
     * Servlet起動時に1回だけ実行される
     */
    @Override
    public void init() throws ServletException {
        super.init();
        this.service = new SensorDataService();
        System.out.println("✅ SensorDataServlet を起動しました");
    }
    
    /**
     * GETリクエストの処理
     * 最新のセンサーデータを返す
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CORS設定（別のドメインからアクセスを許可）
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        // レスポンスの設定
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // 最新データを取得
            SensorData data = service.getLatestSensorData();
            
            // カンマ区切りで返す（例: "8.5,150.3"）
            String responseText = data.getSpeed() + "," + data.getDistance();
            
            // レスポンスを書き込む
            PrintWriter out = response.getWriter();
            out.print(responseText);
            
            System.out.println("✅ センサーデータを返しました: " + responseText);
            
        } catch (Exception e) {
            System.out.println("❌ エラー: " + e.getMessage());
            response.setStatus(500);  // エラーステータス
            PrintWriter out = response.getWriter();
            out.print("0.0,0.0");
        }
    }
    
    /**
     * POSTリクエストの処理
     * 新しいセンサーデータを保存
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
            
            // パラメータが無い場合はエラー
            if (speedParam == null || distanceParam == null) {
                response.setStatus(400);  // Bad Request
                PrintWriter out = response.getWriter();
                out.print("{\"error\":\"speedとdistanceが必要です\"}");
                return;
            }
            
            // 数値に変換
            double speed = Double.parseDouble(speedParam);
            double distance = Double.parseDouble(distanceParam);
            
            // データを保存
            int id = service.saveSensorData(speed, distance);
            
            // レスポンスを返す
            PrintWriter out = response.getWriter();
            out.print("{\"success\":true,\"id\":" + id + "}");
            
        } catch (NumberFormatException e) {
            response.setStatus(400);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"数値が不正です\"}");
        } catch (Exception e) {
            response.setStatus(500);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"サーバーエラー\"}");
        }
    }
}
