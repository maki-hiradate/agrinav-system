-- PostgreSQL用のテーブル作成スクリプト

-- ユーザーテーブル
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100),
    role VARCHAR(20) DEFAULT 'farmer',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 圃場テーブル
CREATE TABLE IF NOT EXISTS fields (
    field_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    field_name VARCHAR(100) NOT NULL,
    area DECIMAL(10, 2),
    location VARCHAR(255),
    soil_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 作物テーブル
CREATE TABLE IF NOT EXISTS crops (
    crop_id SERIAL PRIMARY KEY,
    field_id INTEGER REFERENCES fields(field_id) ON DELETE CASCADE,
    crop_name VARCHAR(100) NOT NULL,
    variety VARCHAR(100),
    planting_date DATE,
    expected_harvest_date DATE,
    status VARCHAR(20) DEFAULT 'growing',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 作業記録テーブル
CREATE TABLE IF NOT EXISTS work_records (
    record_id SERIAL PRIMARY KEY,
    field_id INTEGER REFERENCES fields(field_id) ON DELETE CASCADE,
    crop_id INTEGER REFERENCES crops(crop_id) ON DELETE SET NULL,
    work_date DATE NOT NULL,
    work_type VARCHAR(50) NOT NULL,
    description TEXT,
    hours_spent DECIMAL(5, 2),
    cost DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 気象データテーブル
CREATE TABLE IF NOT EXISTS weather_data (
    weather_id SERIAL PRIMARY KEY,
    field_id INTEGER REFERENCES fields(field_id) ON DELETE CASCADE,
    recorded_date DATE NOT NULL,
    temperature DECIMAL(5, 2),
    humidity DECIMAL(5, 2),
    rainfall DECIMAL(7, 2),
    wind_speed DECIMAL(5, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- センサーデータテーブル
CREATE TABLE IF NOT EXISTS sensor_data (
    sensor_id SERIAL PRIMARY KEY,
    field_id INTEGER REFERENCES fields(field_id) ON DELETE CASCADE,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    temperature DECIMAL(5, 2),
    humidity DECIMAL(5, 2),
    soil_moisture DECIMAL(5, 2),
    light_intensity DECIMAL(7, 2)
);

-- 行動記録テーブル
CREATE TABLE IF NOT EXISTS trip_data (
    trip_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    field_id INTEGER REFERENCES fields(field_id) ON DELETE SET NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    distance DECIMAL(10, 2),
    route_data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- テストユーザーの作成
INSERT INTO users (username, password, email, full_name, role) 
VALUES ('testuser', 'password123', 'test@example.com', 'Test User', 'farmer')
ON CONFLICT (username) DO NOTHING;