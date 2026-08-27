# 拾光橘 - Cordova 打包项目

## 📱 应用说明

**拾光橘** 是一款高效的时间记录与复盘工具，支持：
- 多类别计时（工作/学习/运动/休息等）
- 数据可视化（日/周/月/年趋势图 + 饼图）
- 任务清单管理
- 每日/每周/每月/每年复盘
- 吉祥物系统（IndexedDB 存储图片）
- **悬浮窗**：透明可折叠，可在其他应用上方控制计时
- **后台持续计时**：前台服务保活，不杀后台

## 🔐 权限说明

| 权限 | 用途 | 是否必需 |
|------|------|----------|
| FOREGROUND_SERVICE | 后台持续计时 | ✅ 必需 |
| FOREGROUND_SERVICE_DATA_SYNC | 前台服务类型 | ✅ 必需 |
| READ_MEDIA_IMAGES | 上传吉祥物图片 | ✅ 必需 |
| RECEIVE_BOOT_COMPLETED | 开机自启恢复服务 | ✅ 推荐 |
| REQUEST_IGNORE_BATTERY_OPTIMIZATIONS | 不杀后台 | ✅ 推荐 |
| INTERNET | ❌ 已移除 | 不需要 |
| ACCESS_LOCATION | ❌ 已移除 | 不需要 |
| CAMERA | ❌ 已移除 | 不需要 |
| RECORD_AUDIO | ❌ 已移除 | 不需要 |
| READ_CONTACTS | ❌ 已移除 | 不需要 |
| CALL_PHONE | ❌ 已移除 | 不需要 |
| SMS | ❌ 已移除 | 不需要 |
| BLUETOOTH/NFC | ❌ 已移除 | 不需要 |

## 🏗️ 项目结构

```
shiguangju/
├── index.html              # 主应用（已添加悬浮窗功能）
├── manifest.json           # PWA 清单
├── sw.js                   # Service Worker（离线缓存）
├── config.xml              # Cordova 核心配置
├── plugin.xml              # Cordova 插件定义
├── timer-service.js        # JS 桥接层
├── TimerServicePlugin.java # Java 桥接层
├── TimerForegroundService.java  # 前台计时服务
├── BootReceiver.java       # 开机自启
├── android-manifest-permissions.xml  # 权限清单
├── icon-192.png           # 应用图标 192x192
├── icon-512.png           # 应用图标 512x512
├── icon_source_1.png      # 原始图标素材
├── icon_source_2.png      # 原始图标素材
└── build.sh               # 一键构建脚本
```

## 🚀 构建步骤

### 环境要求
- Node.js >= 16
- Cordova CLI >= 10
- Android Studio + Android SDK (API 24-34)
- JDK 11 或 17

### 一键构建
```bash
# 1. 安装 Cordova
npm install -g cordova

# 2. 进入项目目录
cd shiguangju

# 3. 运行构建脚本
chmod +x build.sh
./build.sh
```

### 手动构建
```bash
# 创建 Cordova 项目
cordova create build com.shiguangju.app Shiguangju

# 复制 web 资源
cp index.html build/www/
cp manifest.json build/www/
cp sw.js build/www/
cp icon-*.png build/www/

# 复制插件
mkdir -p build/plugins/com.shiguangju.timer/www
mkdir -p build/plugins/com.shiguangju.timer/src/android
cp timer-service.js build/plugins/com.shiguangju.timer/www/
cp TimerServicePlugin.java build/plugins/com.shiguangju.timer/src/android/
cp TimerForegroundService.java build/plugins/com.shiguangju.timer/src/android/
cp BootReceiver.java build/plugins/com.shiguangju.timer/src/android/
cp plugin.xml build/plugins/com.shiguangju.timer/

# 复制 config.xml
cp config.xml build/

# 进入构建目录
cd build

# 添加平台
cordova platform add android

# 添加插件
cordova plugin add ../plugins/com.shiguangju.timer

# 构建 APK（调试版）
cordova build android --debug

# 构建 APK（发布版，需要先配置签名）
cordova build android --release
```

## ⚙️ 不杀后台设置指引

安装后用户需要手动设置（应用内会引导）：

1. **电池优化白名单**：设置 → 电池 → 电池优化 → 找到"拾光橘" → 选择"不允许"
2. **自启动**：设置 → 应用管理 → 拾光橘 → 自启动 → 开启
3. **后台锁定**：最近任务卡片下拉锁定
4. **通知栏锁定**：长按通知 → 锁定通知

## 🎨 悬浮窗使用

- 点击右上角 📌 按钮显示/隐藏悬浮窗
- 悬浮窗可拖拽到屏幕任意位置
- 点击 ◀ 按钮折叠/展开
- 折叠后只显示一个拖拽条，极小占用
- 点击 ▶ 切换计时开始/暂停
- 点击 📂 切换计时类别

## 📝 与原版差异

**未做任何功能修改**，仅新增：
1. 透明可折叠悬浮窗（float-window）
2. Cordova 前台服务集成
3. 权限最小化配置

## 📄 License

MIT
