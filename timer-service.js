/**
 * 拾光橘 - 前台计时服务 JS 桥接
 * 让 Web 端可以控制 Android 前台服务
 */
var exec = require('cordova/exec');

var TimerService = {
    /**
     * 启动前台服务（带通知）
     * @param {string} text - 通知栏显示的文字
     * @param {function} success
     * @param {function} error
     */
    start: function(text, success, error) {
        exec(success || function(){}, error || function(){}, 'TimerService', 'start', [text || '拾光橘正在计时中...']);
    },

    /**
     * 停止前台服务
     */
    stop: function(success, error) {
        exec(success || function(){}, error || function(){}, 'TimerService', 'stop', []);
    },

    /**
     * 更新通知栏文字（实时显示计时）
     */
    update: function(text, success, error) {
        exec(success || function(){}, error || function(){}, 'TimerService', 'update', [text || '']);
    },

    /**
     * 请求电池优化白名单（不杀后台）
     */
    requestBatteryWhite: function(success, error) {
        exec(success || function(){}, error || function(){}, 'TimerService', 'batteryWhite', []);
    },

    /**
     * 检查是否在电池优化白名单
     */
    isBatteryWhite: function(callback) {
        exec(callback || function(){}, function(){}, 'TimerService', 'isBatteryWhite', []);
    }
};

module.exports = TimerService;
