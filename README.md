# AgriNav System

農業用走行データ管理ダッシュボード

リアルタイムで農業機械の速度・距離・位置情報を可視化するWebアプリケーションです。
現在はダミーデータを使用したデモンストレーション版です。

## 📸 スクリーンショット

![AgriNav System Dashboard](https://github.com/user-attachments/assets/ebb03255-5802-46ef-956b-08d6e336dfb4)
## 🎥 デモ動画

[▶️ デモ動画を見る](https://youtu.be/RtYDaqQ_QQw)

## ✨ 主な機能

- 📊 **リアルタイムデータ表示** - 速度と距離をメーター形式で表示
- 🗺️ **地図表示** - Leaflet.jsを使用した走行ルートの可視化
- 📈 **グラフ表示** - Chart.jsによる速度・距離の推移グラフ
- 💾 **データベース連携** - MySQLによるデータの永続化
- 🎨 **モダンなUI** - グラデーションとアニメーションを使用したデザイン

⭐ ## 🔍 データについて

現在のバージョンでは、**テスト用のダミーデータ**を使用しています。

- データベースに事前に挿入されたサンプルデータを表示
- 実際のセンサーやGPS機器との連携機能は未実装
- API機能は実装済みのため、実際のデバイスからのデータ送信にも対応可能

## 🛠️ 技術スタック

### バックエンド
- **Java 17**
- **Jakarta Servlet 6.0**
- **MySQL 8.0**
- **Maven** - ビルドツール

### フロントエンド
- **HTML5 / CSS3**
- **JavaScript (ES6+)**
- **Leaflet.js** - 地図表示
- **Chart.js** - グラフ表示

### 開発環境
- **Eclipse IDE**
- **Apache Tomcat 10**
- **MySQL Workbench**

## 🏗️ アーキテクチャ

MVCアーキテクチャを採用し、レイヤー構造で実装しています。
```
Model (データ構造)
  ↓
Repository (データベース操作)
  ↓
Service (ビジネスロジック)
  ↓
Controller (APIエンドポイント)
  ↓
View (フロントエンド)
```

### ディレクトリ構造
```
agrinav-system/
├── src/main/java/com/agriguide/
│   ├── model/           # データモデル
│   ├── repository/      # データベース操作
│   ├── service/         # ビジネスロジック
│   ├── controller/      # Servlet (API)
│   └── util/            # ユーティリティ
└── src/main/webapp/
    ├── css/             # スタイルシート
    ├── js/              # JavaScript
    ├── index.html       # メインページ
    └── WEB-INF/         # 設定ファイル
```

## 💻 ローカル環境での実行

### 必要な環境

- JDK 17 以上
- Apache Tomcat 10 以上
- MySQL 8.0 以上
- Maven 3.8 以上

### セットアップ手順

1. **リポジトリをクローン**
```bash
git clone https://github.com/maki-hiradate/agrinav-system.git
cd agrinav-system
```

2. **データベースを作成**
```sql
CREATE DATABASE agrinav_db;
USE agrinav_db;

-- テーブル作成とダミーデータ挿入のSQLを実行
-- (詳細はSETUP_GUIDE.mdを参照)
```

3. **データベース接続情報を設定**
```java
// src/main/java/com/agriguide/util/DatabaseUtil.java
private static final String DB_PASSWORD = "あなたのパスワード";
```

4. **ビルド**
```bash
mvn clean install
```

5. **デプロイ**
```
target/agrinav-system.war を Tomcat の webapps フォルダにコピー
```

6. **アクセス**
```
http://localhost:8080/agrinav-system/
```

## 📚 学んだこと

このプロジェクトを通じて、以下の技術・概念を習得しました：

- ✅ **フルスタックWeb開発** - バックエンドからフロントエンドまで一貫した実装
- ✅ **MVCアーキテクチャ** - レイヤー分離による保守性の向上
- ✅ **RESTful API設計** - HTTPメソッドを活用したAPI設計
- ✅ **データベース設計** - 正規化とインデックス設計
- ✅ **Maven** - 依存関係管理とビルドプロセスの自動化
- ✅ **Git / GitHub** - バージョン管理とコード公開

## 🎯 今後の展望

- [ ] **完全なレスポンシブデザイン** - スマホ・タブレット最適化
- [ ] **実センサー連携** - IoTデバイスからのリアルタイムデータ取得
- [ ] リアルタイムデータ保存機能の実装
- [ ] ユーザー認証機能の追加
- [ ] データエクスポート機能（CSV/PDF）
- [ ] 複数ユーザー対応
- [ ] WebSocket によるリアルタイム更新

## 📄 ライセンス

MIT License

## 👤 作成者

Maki Hiradate

## 🙏 謝辞

このプロジェクトは学習目的で作成されました。
