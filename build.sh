#!/bin/bash
set -e

echo "=========================================="
echo "  拾光橘 - Cordova APK 构建脚本"
echo "=========================================="
echo ""

# 检查环境
echo "🔍 检查构建环境..."
command -v node >/dev/null 2>&1 || { echo "❌ 需要安装 Node.js"; exit 1; }
command -v cordova >/dev/null 2>&1 || { echo "📦 安装 Cordova..."; npm install -g cordova; }
command -v java >/dev/null 2>&1 || { echo "❌ 需要安装 JDK 11/17"; exit 1; }

echo "✅ 环境检查通过"
echo ""

# 清理旧构建
if [ -d "build" ]; then
    echo "🧹 清理旧构建..."
    rm -rf build
fi

# 创建 Cordova 项目
echo "🏗️ 创建 Cordova 项目..."
cordova create build com.shiguangju.app Shiguangju

# 复制 Web 资源
echo "📋 复制应用资源..."
cp index.html build/www/
cp manifest.json build/www/
cp sw.js build/www/
cp icon-192.png build/www/
cp icon-512.png build/www/

# 复制插件
echo "🔌 安装自定义插件..."
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

# 添加 Android 平台
echo "📱 添加 Android 平台..."
cordova platform add android@12

# 添加插件
echo "🔌 添加插件..."
cordova plugin add ../plugins/com.shiguangju.timer

# 检查 Android SDK
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
    echo "⚠️ 警告: ANDROID_HOME 未设置"
    echo "  请先安装 Android Studio 并设置环境变量"
    echo "  export ANDROID_HOME=~/Android/Sdk"
    echo ""
fi

# 构建 Debug APK
echo "🔨 构建 Debug APK..."
cordova build android --debug

# 检查产物
APK_PATH="platforms/android/app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    cp "$APK_PATH" ../shiguangju-debug.apk
    echo ""
    echo "=========================================="
    echo "  ✅ 构建成功！"
    echo "  📄 APK: shiguangju-debug.apk"
    echo "=========================================="
else
    echo "❌ APK 未生成，请检查错误日志"
    exit 1
fi

cd ..
