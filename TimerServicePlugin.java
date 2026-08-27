package com.shiguangju.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Cordova 插件桥接 —— 连接 JS 和 Android 原生
 */
public class TimerServicePlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "start":
                String text = args.optString(0, "拾光橘正在计时中...");
                TimerForegroundService.startService(cordova.getActivity(), text);
                callbackContext.success("started");
                return true;

            case "stop":
                TimerForegroundService.stopService(cordova.getActivity());
                callbackContext.success("stopped");
                return true;

            case "update":
                String updateText = args.optString(0, "");
                TimerForegroundService.updateService(cordova.getActivity(), updateText);
                callbackContext.success("updated");
                return true;

            case "batteryWhite":
                requestBatteryOptimizationWhiteList(callbackContext);
                return true;

            case "isBatteryWhite":
                boolean isWhite = isIgnoringBatteryOptimizations();
                PluginResult result = new PluginResult(PluginResult.Status.OK, isWhite);
                callbackContext.sendPluginResult(result);
                return true;

            default:
                callbackContext.error("Unknown action: " + action);
                return false;
        }
    }

    /**
     * 请求加入电池优化白名单
     */
    private void requestBatteryOptimizationWhiteList(CallbackContext callbackContext) {
        Activity activity = cordova.getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(intent);
            callbackContext.success("requested");
        } else {
            callbackContext.success("not_needed");
        }
    }

    /**
     * 检查是否已在电池优化白名单中
     */
    private boolean isIgnoringBatteryOptimizations() {
        Activity activity = cordova.getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) activity.getSystemService(Activity.POWER_SERVICE);
            if (pm != null) {
                return pm.isIgnoringBatteryOptimizations(activity.getPackageName());
            }
        }
        return true; // 低于 M 版本不需要
    }
}
