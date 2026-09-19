# NekoSU 主题商城

欢迎来到 NekoSU 主题商城！这里是用户分享和下载自定义主题布局的地方。

## 如何提交你的主题

1. Fork 这个仓库
2. 在 `Vive` 文件夹中创建你的主题 JSON 文件
3. （可选）在 `Vive/previews` 文件夹中添加预览图片
4. 创建 Pull Request

## 主题文件格式

主题文件必须是 JSON 格式，包含以下字段：

```json
{
  "name": "主题名称",
  "description": "主题描述",
  "author": "你的名字",
  "version": "1.0.0",
  "previewUrl": "预览图片URL（可选）",
  "downloads": 0,
  "layout": {
    "wallpaperPath": null,
    "wallpaperAlpha": 0.5,
    "modules": [
      {
        "id": "唯一ID",
        "type": "模块类型",
        "enabled": true,
        "position": 0
      }
    ]
  }
}
```

## 可用的模块类型

- `STATUS_CARD` - 状态卡片
- `UPDATE_CARD` - 更新卡片
- `INFO_CARD` - 信息卡片
- `QUICK_ACTIONS` - 快捷操作
- `DEVICE_INFO` - 设备信息
- `KERNEL_INFO` - 内核信息
- `CUSTOM_TEXT` - 自定义文本
- `CUSTOM_IMAGE` - 自定义图片
- `SEPARATOR` - 分隔符

## 示例主题

查看 `Vive` 文件夹中的示例文件：
- `example-theme.json` - 默认布局
- `minimal-theme.json` - 极简布局
- `full-theme.json` - 完整信息布局

## 注意事项

- 每个模块必须有唯一的 `id`
- `position` 字段决定模块的显示顺序
- `wallpaperPath` 在主题文件中通常设置为 `null`，用户可以自己设置壁纸
- `wallpaperAlpha` 范围是 0.0 到 1.0
- 预览图片建议尺寸：1080x2400（手机截图）

## 主题审核

提交的主题会经过审核，确保：
- JSON 格式正确
- 不包含恶意内容
- 模块配置合理

---

感谢你为 NekoSU 社区做出贡献！
