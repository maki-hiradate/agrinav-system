# AgriNav System - セットアップ完全ガイド

このドキュメントでは、AgriNav Systemを動かすまでの手順を、初心者でもわかるように詳しく説明します。

---

## 📋 必要なもの

### 1. インストールが必要なソフトウェア

- ✅ **JDK 17以上** - Javaの開発環境
- ✅ **Apache Tomcat 10** - Webアプリケーションサーバー
- ✅ **MySQL 8.0以上** - データベース
- ✅ **Eclipse IDE** - 開発環境（お使いのもの）
- ✅ **Maven** - ビルドツール（Eclipseに組み込まれています）

---

## 🚀 ステップ1: ZIPファイルの解凍

### 1-1. ダウンロードしたZIPファイルを解凍

```
agrinav-system.zip を右クリック → 「すべて展開」または「解凍」
```

解凍すると、以下のような構造になります：

```
agrinav-system/
├── pom.xml
├── README.md
├── LEARNING_GUIDE.md
└── src/
    └── main/
        ├── java/
        └── webapp/
```

### 1-2. 解凍先の推奨場所

```
C:\workspace\agrinav-system
または
D:\Projects\agrinav-system
```

---

## 🗄️ ステップ2: データベースのセットアップ

### 2-1. MySQLを起動

**Windowsの場合**:
```bash
# サービスから起動
Win + R → services.msc → MySQL を探して「開始」

# またはコマンドプロンプトから
net start MySQL80
```

**Macの場合**:
```bash
# システム環境設定 → MySQL → Start MySQL Server
# またはターミナルから
mysql.server start
```

### 2-2. MySQLにログイン

コマンドプロンプト（またはターミナル）を開いて：

```bash
mysql -u root -p
```

パスワードを入力してログインします。

### 2-3. データベースとテーブルを作成

以下のSQLを順番に実行します：

```sql
-- 1. データベースを作成
CREATE DATABASE agrinav_db;

-- 2. 使用するデータベースを選択
USE agrinav_db;

-- 3. センサーデータのテーブルを作成
CREATE TABLE sensor_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    speed DOUBLE NOT NULL COMMENT '速度(km/h)',
    distance DOUBLE NOT NULL COMMENT '距離(m)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='センサーデータテーブル';

-- 4. 走行履歴データのテーブルを作成
CREATE TABLE trip_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    speed DOUBLE NOT NULL COMMENT '速度(km/h)',
    distance DOUBLE NOT NULL COMMENT '距離(m)',
    latitude DOUBLE NOT NULL COMMENT '緯度',
    longitude DOUBLE NOT NULL COMMENT '経度',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'タイムスタンプ'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='走行履歴データテーブル';

-- 5. テスト用データを挿入
INSERT INTO sensor_data (speed, distance) VALUES 
    (8.5, 150.3),
    (9.2, 180.5),
    (7.8, 200.1);

INSERT INTO trip_data (speed, distance, latitude, longitude) VALUES 
    (8.5, 150.3, 36.500000, 138.500000),
    (9.2, 180.5, 36.510000, 138.510000),
    (7.8, 200.1, 36.520000, 138.520000),
    (8.1, 220.4, 36.530000, 138.510000),
    (8.8, 240.8, 36.540000, 138.500000),
    (9.5, 260.2, 36.550000, 138.490000),
    (8.3, 280.5, 36.560000, 138.480000),
    (7.9, 300.1, 36.570000, 138.470000),
    (8.6, 320.3, 36.580000, 138.460000),
    (9.1, 340.7, 36.590000, 138.450000);

-- 6. データが入っているか確認
SELECT * FROM sensor_data;
SELECT * FROM trip_data;
```

### 2-4. データベース設定の確認

以下のコマンドで、データベースとテーブルが作成されたことを確認：

```sql
-- データベース一覧を表示
SHOW DATABASES;

-- テーブル一覧を表示
SHOW TABLES;

-- テーブルの構造を確認
DESC sensor_data;
DESC trip_data;
```

---

## 🔧 ステップ3: データベース接続情報の設定

### 3-1. DatabaseUtil.javaを開く

`src/main/java/com/agriguide/util/DatabaseUtil.java` を開きます。

### 3-2. 接続情報を確認・変更

```java
// あなたの環境に合わせて変更してください
private static final String DB_URL = "jdbc:mysql://localhost:3306/agrinav_db";
private static final String DB_USER = "root";  // ← あなたのユーザー名
private static final String DB_PASSWORD = "root1234";  // ← あなたのパスワード
```

**よくあるパターン**:
- XAMPPを使っている場合: ユーザー名 `root`、パスワードなし（空文字 `""`）
- MAMPを使っている場合: ユーザー名 `root`、パスワード `root`
- 独自に設定した場合: 自分で設定したユーザー名とパスワード

---

## 📦 ステップ4: Eclipseへのインポート

### 4-1. Eclipseを起動

### 4-2. プロジェクトをインポート

1. **File** → **Import** をクリック
2. **Maven** → **Existing Maven Projects** を選択
3. **Next** をクリック
4. **Root Directory** の **Browse** をクリック
5. 解凍した `agrinav-system` フォルダを選択
6. **Finish** をクリック

### 4-3. プロジェクトが読み込まれるまで待つ

右下に「Building workspace」と表示されます。
これが終わるまで待ちます（初回は数分かかることがあります）。

### 4-4. エラーが出た場合

**プロジェクトに赤い×マークが付いている場合**:

1. プロジェクトを右クリック
2. **Maven** → **Update Project** を選択
3. **Force Update of Snapshots/Releases** にチェック
4. **OK** をクリック

---

## 🌐 ステップ5: Tomcatの設定

### 5-1. Tomcatサーバーの追加（初回のみ）

**Eclipseの下部にある「Servers」タブ**で：

1. 「No servers are available. Click this link to create a new server...」をクリック
2. **Apache** → **Tomcat v10.1 Server** を選択
3. **Next** をクリック
4. **Tomcat installation directory** で、Tomcatをインストールしたフォルダを選択
5. **Finish** をクリック

### 5-2. プロジェクトをTomcatに追加

1. 「Servers」タブで **Tomcat v10.1 Server at localhost** を右クリック
2. **Add and Remove...** を選択
3. 左側の **Available** から **agrinav-system** を選択
4. **Add >** をクリックして右側に移動
5. **Finish** をクリック

---

## ▶️ ステップ6: アプリケーションの起動

### 6-1. Tomcatサーバーを起動

**Serversタブ**で：

1. **Tomcat v10.1 Server at localhost** を右クリック
2. **Start** をクリック

または、サーバーを選択して上部の緑の▶️ボタンをクリック

### 6-2. 起動を確認

**Console**タブに以下のようなメッセージが表示されればOK：

```
情報: サーバーの起動 [xxxx] ミリ秒
✅ MySQLドライバーのロードに成功しました
✅ SensorDataServlet を起動しました
✅ HistoryDataServlet を起動しました
```

### 6-3. エラーが出た場合

**よくあるエラーと対処法**:

#### エラー1: ポート8080が既に使用されている
```
Port 8080 required by Tomcat v10.1 Server is already in use
```

**対処法**:
1. 他のTomcatやアプリケーションを停止
2. または、Tomcatのポート番号を変更（8081など）

#### エラー2: MySQLに接続できない
```
❌ データベース接続エラー
```

**対処法**:
1. MySQLが起動しているか確認
2. DatabaseUtil.javaの接続情報を確認
3. データベース `agrinav_db` が作成されているか確認

---

## 🖥️ ステップ7: ブラウザで確認

### 7-1. ブラウザを開く

Chrome、Firefox、Edgeなど、お好きなブラウザを使用します。

### 7-2. URLにアクセス

以下のURLをアドレスバーに入力：

```
http://localhost:8080/agrinav-system/
```

### 7-3. 表示されるべき画面

- ✅ 「AgriNav System Dashboard」のタイトル
- ✅ 緑色のLEDバー
- ✅ 速度と距離のメーター
- ✅ 日本の地図
- ✅ 「開始」「停止」「リセット」ボタン
- ✅ 速度と距離のグラフ

### 7-4. データが表示されない場合

**ブラウザの開発者ツールを開いて確認**:

1. **F12** キーを押す（または右クリック→「検証」）
2. **Console** タブを選択
3. エラーメッセージを確認

**よくあるエラー**:

#### エラー: CORS error
```
Access to fetch at '...' from origin '...' has been blocked by CORS policy
```

**対処法**: Servletファイルが正しく起動しているか確認

#### エラー: 404 Not Found
```
GET http://localhost:8080/agrinav-system/api/sensor-data 404
```

**対処法**:
1. Tomcatが起動しているか確認
2. プロジェクトがTomcatにデプロイされているか確認

---

## 🎯 ステップ8: 動作確認

### 8-1. 基本機能のテスト

1. **開始ボタン**をクリック
   - LEDバーが点灯し始める
   - 速度と距離の数値が増える

2. **停止ボタン**をクリック
   - アニメーションが止まる

3. **リセットボタン**をクリック
   - 数値が0に戻る

### 8-2. データベースからのデータ取得確認

1. ページを更新（F5）
2. 速度と距離のグラフにデータが表示される
3. グラフに10個の棒または線が表示される

### 8-3. データベースに新しいデータを追加

MySQLで以下を実行：

```sql
USE agrinav_db;

INSERT INTO sensor_data (speed, distance) VALUES (10.5, 400.0);
INSERT INTO trip_data (speed, distance, latitude, longitude) 
VALUES (10.5, 400.0, 36.600000, 138.440000);
```

ブラウザを更新して、新しいデータがグラフに表示されることを確認。

---

## 📚 ステップ9: コードを読んで理解する

### 推奨の学習順序

1. **README.md** を読む
   - プロジェクト全体の理解

2. **LEARNING_GUIDE.md** を読む
   - 各レイヤーの詳細説明

3. **Modelクラス**から読む
   - `SensorData.java`
   - `TripData.java`

4. **Utilクラス**を読む
   - `DatabaseUtil.java`
   - `JsonUtil.java`

5. **Repositoryクラス**を読む
   - `SensorDataRepository.java`
   - `TripDataRepository.java`

6. **Serviceクラス**を読む
   - `SensorDataService.java`
   - `TripDataService.java`

7. **Controllerクラス**を読む
   - `SensorDataServlet.java`
   - `HistoryDataServlet.java`

8. **フロントエンド**を読む
   - `index.html`
   - `app.js`

### コードを読む際のポイント

- ✅ 各クラスの先頭にあるコメントを読む
- ✅ メソッドの上のコメントを読む
- ✅ わからないところはメモして、後で調べる
- ✅ デバッグして実際の動きを確認する

---

## 🔧 ステップ10: カスタマイズに挑戦

理解が深まったら、以下のカスタマイズに挑戦してみましょう：

### 初級編

1. **色を変更**
   - `style.css` の色コードを変更
   - 例: `#27ae60` → `#e74c3c`（緑→赤）

2. **タイトルを変更**
   - `index.html` のタイトルを自分の好きな名前に

3. **地図の初期位置を変更**
   - `app.js` の `[36.0, 138.0]` を自分の住んでいる地域の座標に

### 中級編

4. **新しいメーターを追加**
   - 例: 走行時間、平均速度など

5. **データの削除機能を追加**
   - DELETEメソッドのServletを作成

6. **データの編集機能を追加**
   - PUTメソッドのServletを作成

### 上級編

7. **ユーザー認証機能を追加**
   - ログイン・ログアウト機能

8. **複数ユーザー対応**
   - ユーザーごとにデータを管理

9. **リアルタイム更新**
   - WebSocketを使った自動更新

---

## ❓ トラブルシューティング

### Q1: Eclipseでプロジェクトが認識されない

**A**: 以下を試してください：

1. プロジェクトを右クリック → **Configure** → **Convert to Maven Project**
2. プロジェクトを右クリック → **Maven** → **Update Project**
3. Eclipse を再起動

### Q2: Tomcatが起動しない

**A**: 以下を確認してください：

1. Tomcatが正しくインストールされているか
2. ポート8080が他のアプリケーションで使用されていないか
3. JDKのバージョンが17以上か

### Q3: データベースに接続できない

**A**: 以下を確認してください：

1. MySQLが起動しているか
2. ユーザー名とパスワードが正しいか
3. データベース `agrinav_db` が作成されているか
4. MySQL Connector/Jが正しく配置されているか

### Q4: 画面が真っ白

**A**: 以下を確認してください：

1. ブラウザの開発者ツール（F12）でエラーを確認
2. CSSファイルが正しく読み込まれているか
3. JavaScriptのエラーがないか

### Q5: グラフが表示されない

**A**: 以下を確認してください：

1. Chart.jsが読み込まれているか（開発者ツールのNetworkタブで確認）
2. データベースにデータが入っているか
3. APIが正しくデータを返しているか（`/api/history-data` に直接アクセス）

---

## 📞 次のステップ

このガイドを完了したら：

1. ✅ **README.md** と **LEARNING_GUIDE.md** を熟読
2. ✅ コードを読んで、各クラスの役割を理解
3. ✅ 簡単なカスタマイズに挑戦
4. ✅ 新しい機能を追加してみる
5. ✅ GitHubにアップロードしてポートフォリオに追加

頑張ってください！🚀

---

## 📝 メモ欄

気づいたことや質問をここにメモしておきましょう：

```
・
・
・
```
