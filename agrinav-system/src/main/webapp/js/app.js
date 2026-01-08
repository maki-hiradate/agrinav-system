// ========================================
// 変数の定義
// ========================================

// 速度と距離の値
let speed = 0;
let distance = 0;

// 動作中かどうか
let isRunning = false;

// アニメーションのID
let animationId = null;

// グラフのオブジェクト
let speedChart = null;
let distanceChart = null;

// ========================================
// ページ読み込み時の処理
// ========================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('✅ ページが読み込まれました');
    
    // 地図を初期化
    initializeMap();
    
    // グラフを初期化
    initializeCharts();
    
    // ボタンにイベントを設定
    setupButtons();
    
    // 定期的にデータを取得（3秒ごと）
    setInterval(fetchSensorData, 3000);
    
    // 定期的にグラフを更新（10秒ごと）
    setInterval(fetchHistoryData, 10000);
    
    // 初回のデータ取得
    fetchSensorData();
    fetchHistoryData();
});

// ========================================
// ボタンのイベント設定
// ========================================

function setupButtons() {
    // 開始ボタン
    document.getElementById('startBtn').addEventListener('click', function() {
        console.log('▶️ 開始ボタンが押されました');
        start();
    });
    
    // 停止ボタン
    document.getElementById('stopBtn').addEventListener('click', function() {
        console.log('⏸️ 停止ボタンが押されました');
        stop();
    });
    
    // リセットボタン
    document.getElementById('resetBtn').addEventListener('click', function() {
        console.log('🔄 リセットボタンが押されました');
        reset();
    });
}

// ========================================
// アニメーション関連
// ========================================

// 開始
function start() {
    if (!isRunning) {
        isRunning = true;
        animate();
    }
}

// 停止
function stop() {
    isRunning = false;
    if (animationId) {
        cancelAnimationFrame(animationId);
    }
}

// リセット
function reset() {
    stop();
    speed = 0;
    distance = 0;
    updateDisplay();
    updateLEDs(0);
}

// アニメーションのメイン処理
function animate() {
    if (!isRunning) return;
    
    // 速度を少しずつ上げる（最大10km/h）
    if (speed < 10) {
        speed += 0.1;
    }
    
    // 距離を増やす
    distance += speed * 0.01;
    
    // 画面を更新
    updateDisplay();
    updateLEDs(speed);
    
    // 次のフレームを呼び出し
    animationId = requestAnimationFrame(animate);
}

// ========================================
// 画面表示の更新
// ========================================

// 速度と距離の表示を更新
function updateDisplay() {
    document.getElementById('speed').textContent = speed.toFixed(1);
    document.getElementById('distance').textContent = distance.toFixed(1);
}

// LEDバーの更新
function updateLEDs(currentSpeed) {
    const leds = document.querySelectorAll('.led');
    const ledCount = Math.floor((currentSpeed / 10) * leds.length);
    
    leds.forEach((led, index) => {
        if (index < ledCount) {
            led.classList.add('active');
        } else {
            led.classList.remove('active');
        }
    });
}

// ========================================
// バックエンドとの通信
// ========================================

// センサーデータを取得
function fetchSensorData() {
    fetch('http://localhost:8080/agrinav-system/api/sensor-data')
        .then(response => response.text())
        .then(data => {
            console.log('📊 センサーデータを取得:', data);
            
            // カンマで分割（例: "8.5,150.3"）
            const values = data.split(',');
            speed = parseFloat(values[0]);
            distance = parseFloat(values[1]);
            
            // 画面を更新
            updateDisplay();
            updateLEDs(speed);
        })
        .catch(error => {
            console.error('❌ センサーデータ取得エラー:', error);
        });
}

// 履歴データを取得してグラフを更新
function fetchHistoryData() {
    fetch('http://localhost:8080/agrinav-system/api/history-data?limit=10')
        .then(response => response.json())
        .then(data => {
            console.log('📊 履歴データを取得:', data.length + '件');
            
            // データが空の場合は何もしない
            if (!data || data.length === 0) {
                console.log('⚠️ 履歴データが空です');
                return;
            }
            
            // 新しい順で取得されるので、古い順に並び替え
            data.reverse();
            
            // ラベル（1, 2, 3...）
            const labels = data.map((item, index) => (index + 1).toString());
            
            // 速度と距離のデータ
            const speedData = data.map(item => item.speed);
            const distanceData = data.map(item => item.distance);
            
            // グラフを更新
            updateChart(speedChart, labels, speedData);
            updateChart(distanceChart, labels, distanceData);
        })
        .catch(error => {
            console.error('❌ 履歴データ取得エラー:', error);
        });
}

// ========================================
// 地図の初期化
// ========================================

function initializeMap() {
    // 地図を作成（日本の中心）
    const map = L.map('map').setView([36.0, 138.0], 6);
    
    // OpenStreetMapのタイルを追加
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
    }).addTo(map);
    
    // サンプルの走行ルート
    const route = [
        [36.5, 138.5],
        [36.51, 138.51],
        [36.52, 138.52],
        [36.53, 138.51],
        [36.54, 138.50],
        [36.55, 138.49]
    ];
    
    // ルートを青い線で描画
    const polyline = L.polyline(route, {
        color: 'blue',
        weight: 4
    }).addTo(map);
    
    // 地図の表示範囲をルートに合わせる
    map.fitBounds(polyline.getBounds());
    
    // スタート地点のマーカー
    L.marker(route[0]).addTo(map)
        .bindPopup('スタート地点')
        .openPopup();
    
    // ゴール地点のマーカー
    L.marker(route[route.length - 1]).addTo(map)
        .bindPopup('ゴール地点');
    
    console.log('🗺️ 地図を初期化しました');
}

// ========================================
// グラフの初期化
// ========================================

function initializeCharts() {
    // 速度のグラフ
    const speedCtx = document.getElementById('speedChart').getContext('2d');
    speedChart = new Chart(speedCtx, {
        type: 'line',  // 折れ線グラフ
        data: {
            labels: [],  // 最初は空
            datasets: [{
                label: '速度 (km/h)',
                data: [],  // 最初は空
                borderColor: 'rgb(75, 192, 192)',
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    labels: { color: '#fff' }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { color: '#fff' },
                    grid: { color: 'rgba(255, 255, 255, 0.1)' }
                },
                x: {
                    ticks: { color: '#fff' },
                    grid: { color: 'rgba(255, 255, 255, 0.1)' }
                }
            }
        }
    });
    
    // 距離のグラフ
    const distanceCtx = document.getElementById('distanceChart').getContext('2d');
    distanceChart = new Chart(distanceCtx, {
        type: 'bar',  // 棒グラフ
        data: {
            labels: [],
            datasets: [{
                label: '距離 (m)',
                data: [],
                backgroundColor: 'rgba(54, 162, 235, 0.5)',
                borderColor: 'rgb(54, 162, 235)',
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    labels: { color: '#fff' }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { color: '#fff' },
                    grid: { color: 'rgba(255, 255, 255, 0.1)' }
                },
                x: {
                    ticks: { color: '#fff' },
                    grid: { color: 'rgba(255, 255, 255, 0.1)' }
                }
            }
        }
    });
    
    console.log('📊 グラフを初期化しました');
}

// グラフのデータを更新
function updateChart(chart, labels, data) {
    if (chart) {
        chart.data.labels = labels;
        chart.data.datasets[0].data = data;
        chart.update();
    }
}
