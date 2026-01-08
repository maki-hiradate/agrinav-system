# AgriNav System - 初心者向け簡易版

このプロジェクトは、農業用の走行データ管理システムです。
初心者が理解しやすいように、コードを大幅に簡略化しています。

## 📁 ディレクトリ構成

```
agrinav-system/
├── pom.xml                          # Mavenの設定ファイル
├── src/main/
│   ├── java/com/agriguide/
│   │   ├── model/                   # データモデル（データの入れ物）
│   │   │   ├── SensorData.java     # センサーデータ
│   │   │   └── TripData.java       # 走行履歴データ
│   │   │
│   │   ├── repository/              # データベース操作
│   │   │   ├── SensorDataRepository.java
│   │   │   └── TripDataRepository.java
│   │   │
│   │   ├── service/                 # ビジネスロジック
│   │   │   ├── SensorDataService.java
│   │   │   └── TripDataService.java
│   │   │
│   │   ├── controller/              # API（Webから呼ばれる）
│   │   │   ├── SensorDataServlet.java
│   │   │   └── HistoryDataServlet.java
│   │   │
│   │   └── util/                    # 便利な機能
│   │       ├── DatabaseUtil.java   # DB接続
│   │       └── JsonUtil.java       # JSON変換
│   │
│   └── webapp/                      # Webページ
│       ├── index.html               # メインページ
│       ├── css/
│       │   └── style.css            # デザイン
│       ├── js/
│       │   └── app.js               # JavaScript
│       └── WEB-INF/
│           └── web.xml              # Web設定
```

## 🎯 各ファイルの役割

### 1. モデル（Model）- データの入れ物
- **SensorData.java**: 速度と距離のデータを保存
- **TripData.java**: 走行履歴データを保存（速度、距離、緯度、経度）

### 2. リポジトリ（Repository）- データベース操作
- **SensorDataRepository.java**: sensor_dataテーブルからデータを取得・保存
- **TripDataRepository.java**: trip_dataテーブルからデータを取得・保存

### 3. サービス（Service）- ビジネスロジック
- **SensorDataService.java**: センサーデータの検証と処理
- **TripDataService.java**: 走行データの検証と処理

### 4. コントローラー（Controller）- API
- **SensorDataServlet.java**: /api/sensor-data へのリクエストを処理
- **HistoryDataServlet.java**: /api/history-data へのリクエストを処理

### 5. ユーティリティ（Util）- 便利な機能
- **DatabaseUtil.java**: データベースへの接続を管理
- **JsonUtil.java**: JavaオブジェクトをJSONに変換

### 6. フロントエンド
- **index.html**: ダッシュボードのHTML
- **style.css**: デザイン（元のまま）
- **app.js**: JavaScript（コメント追加、簡略化）

## 🔧 セットアップ方法

### 1. データベースの準備

MySQLで以下のテーブルを作成してください：

```sql
-- データベースを作成
CREATE DATABASE agriguide_db;
USE agriguide_db;

-- センサーデータのテーブル
CREATE TABLE sensor_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    speed DOUBLE NOT NULL,
    distance DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 走行履歴データのテーブル
CREATE TABLE trip_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    speed DOUBLE NOT NULL,
    distance DOUBLE NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- テスト用のデータを追加
INSERT INTO sensor_data (speed, distance) VALUES (8.5, 150.3);
INSERT INTO trip_data (speed, distance, latitude, longitude) 
VALUES 
    (8.5, 150.3, 36.5, 138.5),
    (9.2, 180.5, 36.51, 138.51),
    (7.8, 200.1, 36.52, 138.52);
```

### 2. データベース接続情報の変更

`DatabaseUtil.java` を開いて、あなたの環境に合わせて変更してください：

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/agriguide_db";
private static final String DB_USER = "root";  // ← あなたのユーザー名
private static final String DB_PASSWORD = "root1234";  // ← あなたのパスワード
```

### 3. ビルドとデプロイ

```bash
# ビルド
mvn clean package

# TomcatのwebappsフォルダにWARファイルをコピー
cp target/agrinav-system.war /path/to/tomcat/webapps/
```

### 4. 動作確認

ブラウザで以下にアクセス：
```
http://localhost:8080/agrinav-system/
```

## 📖 コードの読み方

### 初心者向けの読む順番

1. **Model（モデル）** から読む
   - SensorData.java
   - TripData.java
   - → データの構造を理解する

2. **Util（ユーティリティ）** を読む
   - DatabaseUtil.java
   - JsonUtil.java
   - → 共通機能を理解する

3. **Repository（リポジトリ）** を読む
   - SensorDataRepository.java
   - TripDataRepository.java
   - → データベース操作を理解する

4. **Service（サービス）** を読む
   - SensorDataService.java
   - TripDataService.java
   - → ビジネスロジックを理解する

5. **Controller（コントローラー）** を読む
   - SensorDataServlet.java
   - HistoryDataServlet.java
   - → リクエスト処理を理解する

6. **Frontend（フロントエンド）** を読む
   - index.html
   - app.js
   - → 画面表示を理解する

## 🎓 学習ポイント

### この簡易版で学べること

1. **MVCアーキテクチャ**
   - Model: データの構造
   - View: 画面表示（HTML/CSS/JS）
   - Controller: リクエスト処理（Servlet）

2. **3層アーキテクチャ**
   - Presentation層: Servlet
   - Business層: Service
   - Data層: Repository

3. **データベース操作**
   - JDBCを使った基本的なCRUD操作
   - PreparedStatementの使い方
   - リソースの適切なクローズ

4. **REST API**
   - GETリクエストの処理
   - POSTリクエストの処理
   - JSONレスポンスの返し方

5. **フロントエンド**
   - fetchを使ったAPI呼び出し
   - Chart.jsを使ったグラフ表示
   - Leafletを使った地図表示

## 💡 元のコードとの違い

### 簡略化した点

1. **エラーハンドリング**
   - try-catchを基本的なものだけに
   - エラーメッセージをシンプルに

2. **バリデーション**
   - 必要最小限のチェックのみ

3. **コメント**
   - 日本語で詳しく説明
   - 初心者が理解しやすい表現

4. **コード量**
   - 不要な機能を削除
   - 1ファイルあたり100行前後に

## 🚀 次のステップ

このコードを理解したら、以下にチャレンジしてみましょう：

1. エラーハンドリングを強化
2. ロギング機能の追加
3. ユニットテストの作成
4. より高度なバリデーション
5. セキュリティ対策の追加

## ❓ よくある質問

### Q1: データベースに接続できません
A: DatabaseUtil.javaの接続情報を確認してください。
   また、MySQLが起動しているか確認してください。

### Q2: 画面にデータが表示されません
A: ブラウザの開発者ツール（F12）でコンソールを確認してください。
   エラーメッセージが表示されているはずです。

### Q3: Tomcatにデプロイできません
A: pom.xmlの設定とJavaのバージョンを確認してください。
   Java 17以上が必要です。

## 📝 ライセンス

このプロジェクトは学習用です。自由に改変・使用してください。
