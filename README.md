# EasyMenu

一款基于 Android 的菜单管理应用程序。

## 介绍

EasyMenu 是一个用 Java 开发的 Android 应用，用于管理、编辑和展示餐饮或其他类型的菜单。目标是提供一个轻量、易用且可扩展的菜单管理解决方案，便于商家快速维护菜品、分类和价格等信息。

## 主要特性

- 菜单项管理（添加/编辑/删除）
- 菜单分类管理
- 可配置的价格与规格
- 本地数据存储（可扩展为远程同步）
- 简洁的 Android 原生界面，使用 Java 编写

## 技术栈

- 语言：Java
- 构建工具：Gradle
- 目标：Android

## 快速开始

先决条件：

- JDK 17 或更高
- Android Studio（推荐）

使用 Android Studio 打开项目：

1. 克隆仓库：

```bash
git clone https://github.com/VANLKL/EasyMenu.git
cd EasyMenu
```

2. 使用 Android Studio 打开项目根目录，等待 Gradle 同步完成。
3. 连接 Android 设备或启动模拟器，点击 Run 运行应用。

或使用命令行构建（适用于 CI 环境）：

```bash
./gradlew assembleDebug
# 或构建 release
./gradlew assembleRelease
```

请根据项目中的 Gradle 配置调整 JDK 和 Android SDK 版本。

## 目录结构

- app/ - Android 应用模块
- app/src/main/java/ - Java 源代码
- app/src/main/res/ - 资源（布局、图片、字符串等）
- build.gradle - 项目构建脚本

## 本地化与自定义

- 修改资源文件可支持多语言。
- 菜单数据保存方式可替换为本地数据库（Room）、文件或网络后端。

## 贡献

欢迎贡献！你可以：

- 提交 Issues 反馈 bug 或提功能建议
- 提交 Pull Request 修复或添加功能

贡献流程建议：

1. Fork 本仓库
2. 新建分支：`git checkout -b feature/your-feature`
3. 提交代码并 push
4. 打开 Pull Request，描述变更并关联 Issue（如有）

## 许可

MIT

## 联系方式

如有问题，请通过 GitHub Issues 联系仓库维护者。


---
