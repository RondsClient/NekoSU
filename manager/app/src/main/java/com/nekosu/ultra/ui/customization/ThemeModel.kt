package com.nekosu.ui.customization

import kotlinx.serialization.Serializable

@Serializable
data class Theme(
    val id: String,
    val name: String,
    val author: String,
    val description: String,
    val version: String,
    val previewUrl: String,
    val downloadUrl: String,
    val wallpaperUrl: String? = null,
    val modules: List<ModuleConfig>,
    val stars: Int = 0,
    val downloads: Int = 0
)

@Serializable
data class ModuleConfig(
    val id: String,
    val type: ModuleType,
    val position: Int,
    val enabled: Boolean = true,
    val customData: Map<String, String> = emptyMap()
)

@Serializable
enum class ModuleType {
    STATUS_CARD,      // 状态卡片
    UPDATE_CARD,      // 更新卡片
    INFO_CARD,        // 信息卡片
    QUICK_ACTIONS,    // 快捷操作
    DEVICE_INFO,      // 设备信息
    KERNEL_INFO,      // 内核信息
    CUSTOM_TEXT,      // 自定义文本
    CUSTOM_IMAGE,     // 自定义图片
    SEPARATOR         // 分隔符
}

@Serializable
data class CustomLayout(
    val id: String = "default",
    val name: String = "Default Layout",
    val wallpaperPath: String? = null,
    val wallpaperAlpha: Float = 0.3f,
    val modules: List<ModuleConfig> = defaultModules()
)

fun defaultModules(): List<ModuleConfig> {
    return listOf(
        ModuleConfig("status", ModuleType.STATUS_CARD, 0),
        ModuleConfig("update", ModuleType.UPDATE_CARD, 1),
        ModuleConfig("info", ModuleType.INFO_CARD, 2),
        ModuleConfig("actions", ModuleType.QUICK_ACTIONS, 3),
        ModuleConfig("device", ModuleType.DEVICE_INFO, 4),
        ModuleConfig("kernel", ModuleType.KERNEL_INFO, 5)
    )
}

@Serializable
data class ThemeMarketResponse(
    val themes: List<Theme>
)
