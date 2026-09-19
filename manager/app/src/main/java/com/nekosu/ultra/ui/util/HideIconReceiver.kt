package com.nekosu.ui.util

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import com.nekosu.R
import com.nekosu.ksuApp
import com.nekosu.ui.MainActivity

/**
 * 拨号器暗码接收器
 * 在拨号界面输入 *#*#888#*#* 触发隐藏/显示应用图标
 */
class HideIconReceiver : BroadcastReceiver() {

    companion object {
        /**
         * 设置应用图标可见性
         * @param context 上下文
         * @param visible true=显示图标, false=隐藏图标
         */
        fun setAppIconVisible(context: Context, visible: Boolean) {
            val packageManager = context.packageManager
            val componentName = ComponentName(context, MainActivity::class.java)

            val newState = if (visible) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }

            try {
                packageManager.setComponentEnabledSetting(
                    componentName,
                    newState,
                    PackageManager.DONT_KILL_APP
                )
            } catch (e: Exception) {
                Toast.makeText(context, "设置图标状态失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SECRET_CODE") {
            toggleAppIcon(context)
        }
    }

    private fun toggleAppIcon(context: Context) {
        val packageManager = context.packageManager
        val componentName = ComponentName(context, MainActivity::class.java)

        // 获取当前状态
        val currentState = packageManager.getComponentEnabledSetting(componentName)

        // 切换状态
        val newState = when (currentState) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> {
                // 隐藏图标
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED -> {
                // 显示图标
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            }
            else -> PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        }

        try {
            packageManager.setComponentEnabledSetting(
                componentName,
                newState,
                PackageManager.DONT_KILL_APP
            )

            // 更新SharedPreferences
            val prefs = ksuApp.getSharedPreferences("settings", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("hide_icon", newState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED).apply()

            // 显示提示
            val message = if (newState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                context.getString(R.string.hide_icon_success)
            } else {
                context.getString(R.string.show_icon_success)
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(context, "切换图标状态失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
