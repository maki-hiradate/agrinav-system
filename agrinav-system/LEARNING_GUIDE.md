# コード説明ガイド - 初心者向け

このドキュメントは、AgriNavシステムのコードを1つ1つ説明します。

## 📚 目次

1. [データの流れ](#データの流れ)
2. [各レイヤーの詳細説明](#各レイヤーの詳細説明)
3. [実際の動作例](#実際の動作例)

---

## データの流れ

```
ブラウザ
   ↓ HTTPリクエスト（GET /api/sensor-data）
Servlet（コントローラー）
   ↓ メソッド呼び出し
Service（ビジネスロジック）
   ↓ メソッド呼び出し
Repository（データベース操作）
   ↓ SQL実行
データベース（MySQL）
   ↓ 結果を返す
Repository
   ↓ Javaオブジェクトに変換
Service
   ↓ 検証・加工
Servlet
   ↓ JSON形式に変換
ブラウザ
```

---

## 各レイヤーの詳細説明

### 1. Model（モデル）レイヤー

**役割**: データの構造を定義する

**SensorData.javaの例**:
```java
public class SensorData {
    private int id;          // データのID番号
    private double speed;    // 速度（km/h）
    private double distance; // 距離（m）
    
    // ゲッター（値を取り出す）
    public double getSpeed() {
        return speed;
    }
    
    // セッター（値を設定する）
    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
```

**なぜ必要？**
- データベースのテーブルとJavaのクラスを対応させる
- データを扱いやすくする
- 型安全性を保つ（間違った型のデータを入れない）

---

### 2. Repository（リポジトリ）レイヤー

**役割**: データベースとのやり取りを担当

**主な処理**:
1. データベースに接続
2. SQL文を実行
3. 結果をJavaオブジェクトに変換

**SensorDataRepository.javaの例**:
```java
public SensorData findLatest() {
    Connection conn = null;  // データベース接続
    PreparedStatement stmt = null;  // SQL文
    ResultSet rs = null;  // 実行結果
    
    try {
        // 1. データベースに接続
        conn = DatabaseUtil.getConnection();
        
        // 2. SQL文を準備
        String sql = "SELECT id, speed, distance FROM sensor_data ORDER BY id DESC LIMIT 1";
        stmt = conn.prepareStatement(sql);
        
        // 3. SQL実行
        rs = stmt.executeQuery();
        
        // 4. 結果をJavaオブジェクトに変換
        if (rs.next()) {
            SensorData data = new SensorData();
            data.setId(rs.getInt("id"));
            data.setSpeed(rs.getDouble("speed"));
            data.setDistance(rs.getDouble("distance"));
            return data;
        }
        
        return new SensorData(0, 0.0, 0.0);  // デフォルト値
        
    } catch (SQLException e) {
        System.out.println("エラー: " + e.getMessage());
        return new SensorData(0, 0.0, 0.0);
    } finally {
        // 5. リソースを閉じる（必ず実行）
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("クローズエラー");
        }
    }
}
```

**ポイント**:
- `try-catch-finally`でエラー処理
- `finally`ブロックで必ずリソースを閉じる
- SQL文は文字列で書く

---

### 3. Service（サービス）レイヤー

**役割**: ビジネスロジック（業務処理）を担当

**主な処理**:
1. データの検証（バリデーション）
2. Repositoryの呼び出し
3. データの加工

**SensorDataService.javaの例**:
```java
public int saveSensorData(double speed, double distance) {
    // 1. データの検証
    if (speed < 0 || speed > 100) {
        System.out.println("速度の値が不正です");
        return 0;  // エラーの場合は0を返す
    }
    
    if (distance < 0 || distance > 100000) {
        System.out.println("距離の値が不正です");
        return 0;
    }
    
    // 2. データを作成
    SensorData data = new SensorData(speed, distance);
    
    // 3. Repositoryに保存を依頼
    return repository.save(data);
}
```

**なぜ必要？**
- データベース操作の前に値をチェック
- 不正なデータを保存しない
- ビジネスルールを1か所で管理

---

### 4. Controller（コントローラー）レイヤー

**役割**: Webリクエストを受け取って処理

**主な処理**:
1. リクエストパラメータの取得
2. Serviceの呼び出し
3. レスポンスの返却

**SensorDataServlet.javaの例**:
```java
@WebServlet("/api/sensor-data")  // このURLにアクセスしたら実行
public class SensorDataServlet extends HttpServlet {
    
    private SensorDataService service;
    
    // GETリクエストの処理
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. CORS設定（別ドメインからのアクセス許可）
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        // 2. レスポンスの設定
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        
        // 3. データを取得
        SensorData data = service.getLatestSensorData();
        
        // 4. カンマ区切りのテキストを作成
        String responseText = data.getSpeed() + "," + data.getDistance();
        
        // 5. レスポンスを返す
        PrintWriter out = response.getWriter();
        out.print(responseText);
    }
    
    // POSTリクエストの処理
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. パラメータを取得
        String speedParam = request.getParameter("speed");
        String distanceParam = request.getParameter("distance");
        
        // 2. 数値に変換
        double speed = Double.parseDouble(speedParam);
        double distance = Double.parseDouble(distanceParam);
        
        // 3. データを保存
        int id = service.saveSensorData(speed, distance);
        
        // 4. JSONレスポンスを返す
        PrintWriter out = response.getWriter();
        out.print("{\"success\":true,\"id\":" + id + "}");
    }
}
```

**ポイント**:
- `@WebServlet`でURLを指定
- `doGet`でGETリクエストを処理
- `doPost`でPOSTリクエストを処理
- `HttpServletRequest`でリクエスト情報を取得
- `HttpServletResponse`でレスポンスを返す

---

### 5. Util（ユーティリティ）レイヤー

**役割**: 共通で使う便利な機能を提供

#### DatabaseUtil.java（データベース接続）

```java
public class DatabaseUtil {
    // 接続情報（定数）
    private static final String DB_URL = "jdbc:mysql://localhost:3306/agriguide_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root1234";
    
    // 接続を取得
    public static Connection getConnection() throws SQLException {
        // MySQLドライバーを読み込む
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        // データベースに接続
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    // 接続を閉じる
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("クローズエラー");
            }
        }
    }
}
```

#### JsonUtil.java（JSON変換）

```java
public class JsonUtil {
    // TripDataをJSON文字列に変換
    public static String toJson(TripData data) {
        return String.format(
            "{\"id\":%d,\"speed\":%.1f,\"distance\":%.1f,\"latitude\":%.6f,\"longitude\":%.6f}",
            data.getId(),
            data.getSpeed(),
            data.getDistance(),
            data.getLatitude(),
            data.getLongitude()
        );
    }
    
    // リストをJSON配列に変換
    public static String toJsonArray(List<TripData> dataList) {
        StringBuilder json = new StringBuilder("[");
        
        for (int i = 0; i < dataList.size(); i++) {
            if (i > 0) json.append(",");
            json.append(toJson(dataList.get(i)));
        }
        
        json.append("]");
        return json.toString();
    }
}
```

---

## 実際の動作例

### 例1: センサーデータを取得する流れ

1. **ブラウザ**:
```javascript
fetch('http://localhost:8080/agrinav-system/api/sensor-data')
```

2. **SensorDataServlet** の `doGet` が呼ばれる

3. **SensorDataService** の `getLatestSensorData()` を呼ぶ

4. **SensorDataRepository** の `findLatest()` を呼ぶ

5. **データベース** からデータ取得
```sql
SELECT id, speed, distance FROM sensor_data ORDER BY id DESC LIMIT 1
```

6. 結果を **SensorData** オブジェクトに変換
```java
SensorData data = new SensorData(1, 8.5, 150.3);
```

7. **Servlet** で文字列に変換
```java
String response = "8.5,150.3";
```

8. **ブラウザ** に返す
```
8.5,150.3
```

9. **JavaScript** で処理
```javascript
const values = data.split(',');
speed = parseFloat(values[0]);  // 8.5
distance = parseFloat(values[1]);  // 150.3
```

---

### 例2: 履歴データを取得する流れ

1. **ブラウザ**:
```javascript
fetch('http://localhost:8080/agrinav-system/api/history-data?limit=10')
```

2. **HistoryDataServlet** の `doGet` が呼ばれる

3. パラメータから件数を取得
```java
int limit = Integer.parseInt(request.getParameter("limit"));  // 10
```

4. **TripDataService** の `getLatestTripData(10)` を呼ぶ

5. **TripDataRepository** の `findLatest(10)` を呼ぶ

6. **データベース** から10件取得
```sql
SELECT id, speed, distance, latitude, longitude 
FROM trip_data ORDER BY id DESC LIMIT 10
```

7. 結果を **List<TripData>** に変換
```java
List<TripData> dataList = new ArrayList<>();
while (rs.next()) {
    TripData data = new TripData(...);
    dataList.add(data);
}
```

8. **JsonUtil** でJSON配列に変換
```java
String json = JsonUtil.toJsonArray(dataList);
// [{"id":1,"speed":8.5,...},{"id":2,"speed":9.2,...},...]
```

9. **ブラウザ** に返す

10. **JavaScript** で処理
```javascript
.then(response => response.json())
.then(data => {
    // data は配列
    data.forEach(item => {
        console.log(item.speed, item.distance);
    });
});
```

---

## 重要な概念

### 1. レイヤー分け（層の分離）

**なぜ分ける？**
- 役割が明確になる
- 変更がしやすい（例: データベースを変えてもRepositoryだけ変更）
- テストがしやすい
- チームで開発しやすい

### 2. try-catch-finally

```java
try {
    // 通常の処理
} catch (Exception e) {
    // エラーが起きたときの処理
} finally {
    // 必ず実行される処理（リソースのクローズなど）
}
```

### 3. PreparedStatement

**通常のStatement（危険）**:
```java
String sql = "SELECT * FROM users WHERE name = '" + userName + "'";
// SQLインジェクションの危険！
```

**PreparedStatement（安全）**:
```java
String sql = "SELECT * FROM users WHERE name = ?";
stmt = conn.prepareStatement(sql);
stmt.setString(1, userName);  // 自動的にエスケープされる
```

### 4. リソースのクローズ

**重要**: Connection、Statement、ResultSetは必ず閉じる

```java
finally {
    try {
        if (rs != null) rs.close();      // ResultSet
        if (stmt != null) stmt.close();  // Statement
        if (conn != null) conn.close();  // Connection
    } catch (SQLException e) {
        // エラーログ
    }
}
```

閉じないと：
- メモリリーク
- 接続数の上限に達する
- データベースが重くなる

---

## まとめ

このシステムは以下の流れでデータを扱います：

1. **Model**: データの構造を定義
2. **Repository**: データベース操作
3. **Service**: ビジネスロジック
4. **Controller**: Webリクエストの処理
5. **Util**: 共通機能

各レイヤーが明確な役割を持つことで、
理解しやすく、保守しやすいコードになっています。

次は実際にコードを読んで、動かしてみましょう！
