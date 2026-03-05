# バックエンド職能課題

## 概要

教師（facilitator）に紐づく生徒一覧を取得するREST APIサーバーです。

## 技術スタック

| 項目 | 技術 |
|---|---|
| 言語 | Java 17 |
| フレームワーク | Spring Boot 3.2.3 |
| ORM | Spring Data JPA (Hibernate) |
| データベース | MySQL 8.0 |
| ビルドツール | Gradle 8.5 |
| コンテナ | Docker / Docker Compose |
| テスト | JUnit 5, Mockito, H2 Database |

## 起動方法

```bash
git clone https://github.com/yuta-toshino/COMPASS-coding-app.git
cd COMPASS-coding-app
docker compose up
```

起動後、以下のポートでアクセスできます：
- **APIサーバー**: http://localhost:48080
- **MySQL**: localhost:43306

## API仕様

### GET /students

教師に紐づく生徒一覧を取得します。

#### リクエストパラメータ

| パラメータ | 型 | 必須 | デフォルト | 説明 |
|---|---|---|---|---|
| `facilitator_id` | int | ○ | - | 教師ID |
| `page` | int | × | 1 | ページ番号 |
| `limit` | int | × | 5 | 1ページあたりの表示数 |
| `sort` | string | × | id | ソートキー（id, name, loginId） |
| `order` | string | × | asc | ソート順（asc, desc） |
| `name_like` | string | × | - | 生徒名の部分一致検索 |
| `loginId_like` | string | × | - | ログインIDの部分一致検索 |

#### リクエスト例

```bash
# 基本リクエスト
curl 'http://127.0.0.1:48080/students?facilitator_id=1'

# ページネーション
curl 'http://127.0.0.1:48080/students?facilitator_id=1&page=2&limit=3'

# ソート（生徒名降順）
curl 'http://127.0.0.1:48080/students?facilitator_id=1&sort=name&order=desc'

# 部分一致検索
curl 'http://127.0.0.1:48080/students?facilitator_id=1&name_like=佐'
```

#### レスポンス例

```json
{
  "students": [
    {
      "id": 1,
      "name": "佐藤",
      "loginId": "foo123",
      "classroom": {
        "id": 1,
        "name": "クラスA"
      }
    }
  ],
  "totalCount": 1
}
```

#### エラーレスポンス

| 状況 | ステータスコード |
|---|---|
| リクエストに該当する生徒が存在しない | 404 Not Found |
| リクエストに問題がある | 400 Bad Request |

## アーキテクチャ

### レイヤードアーキテクチャ

```
Controller層 → Service層 → Repository層 → Database
     ↕             ↕
   DTO層       Entity層
```

- **Controller層**: リクエストの受付・バリデーション・レスポンスの返却
- **Service層**: ビジネスロジック（ページネーション・ソート・検索）
- **Repository層**: Spring Data JPA によるデータアクセス
- **Entity層**: DBテーブルに対応するJPAエンティティ
- **DTO層**: APIレスポンス用のデータ転送オブジェクト

### テーブル設計

正規化した4テーブル構成にしています。

```
teachers (教師マスタ)
  └─ teacher_student_classroom (関連テーブル)
       ├─ students (生徒マスタ)
       └─ classrooms (クラスマスタ)
```

- `teachers`: 教師情報
- `students`: 生徒情報（名前、ログインID）
- `classrooms`: クラス情報
- `teacher_student_classroom`: 教師-生徒-クラスの関連

### 実装方針

1. **N+1問題の回避**: JOIN FETCHにより関連データを一括取得
2. **テストの充実**: ユニットテスト（Mockito）と統合テスト（H2 + MockMvc）の2層
3. **適切なエラーハンドリング**: GlobalExceptionHandlerで統一的なエラーレスポンス
4. **Docker対応**: multi-stage buildで軽量なコンテナイメージを生成

## テスト

### テスト構成

- **ユニットテスト** (`StudentServiceTest`): Mockitoでリポジトリをモックし、サービス層のロジックを検証
- **統合テスト** (`StudentControllerIntegrationTest`): H2インメモリDBでAPI全体の動作を検証

### テスト実行

```bash
cd api
./gradlew test
```

## プロジェクト構成

```
.
├── docker-compose.yml
├── mysql/
│   ├── init/
│   │   ├── 01_schema.sql    # テーブル定義
│   │   └── 02_data.sql      # 初期データ
│   └── my.cnf               # MySQL設定
├── api/
│   ├── Dockerfile
│   ├── build.gradle
│   ├── settings.gradle
│   └── src/
│       ├── main/
│       │   ├── java/com/compass/api/
│       │   │   ├── Application.java
│       │   │   ├── controller/
│       │   │   ├── service/
│       │   │   ├── repository/
│       │   │   ├── entity/
│       │   │   ├── dto/
│       │   │   └── exception/
│       │   └── resources/
│       │       └── application.yml
│       └── test/
└── README.md
```
