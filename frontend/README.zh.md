# AstronRPA 前端平台

<div align="center">

**🎨 现代化 RPA 应用前端开发平台**

[![Node.js](https://img.shields.io/badge/node.js-22+-green.svg)](https://nodejs.org/)
[![Vue](https://img.shields.io/badge/vue-3+-4FC08D.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/typescript-5.9+-blue.svg)](https://www.typescriptlang.org/)
[![pnpm](https://img.shields.io/badge/pnpm-9+-orange.svg)](https://pnpm.io/)
[![Tauri](https://img.shields.io/badge/tauri-1.6+-purple.svg)](https://tauri.app/)

[English](README.md) | 简体中文

</div>

## 📑 目录

- [AstronRPA 前端平台](#astronrpa-前端平台)
  - [📑 目录](#-目录)
  - [📋 概述](#-概述)
  - [✨ 核心特性](#-核心特性)
  - [🛠️ 技术栈](#️-技术栈)
  - [🚀 快速开始](#-快速开始)
    - [系统要求](#系统要求)
    - [开发环境搭建](#开发环境搭建)
    - [构建与部署](#构建与部署)
  - [📦 包结构](#-包结构)
    - [核心包](#核心包)
    - [开发工具](#开发工具)
  - [🏗️ 架构概览](#️-架构概览)
    - [技术栈详情](#技术栈详情)

## 📋 概述

AstronRPA 前端是一个专为 RPA 应用构建的现代化前端平台。它提供了构建基于 Web 和桌面 RPA 应用的完整解决方案，采用统一代码库。

该平台采用 pnpm workspaces 的单体仓库架构，支持多种应用类型，包括 Web 应用、桌面应用（通过 Tauri）和浏览器插件，所有应用共享通用组件和工具。

## ✨ 核心特性

- 🚀 **高性能** - 基于 Vite 的构建系统，支持优化打包和懒加载
- 🔒 **类型安全** - 完整的 TypeScript 支持，严格类型检查
- 🔧 **易于集成** - 模块化包结构，支持工作区依赖
- 📊 **实时开发** - 热模块替换和快速刷新
- 🌍 **多平台支持** - 支持 Web、桌面（Tauri）和浏览器扩展
- 📈 **可扩展架构** - 单体仓库，共享组件和工具

## 🛠️ 技术栈

**前端框架**: Vue 3 + TypeScript + Vite
**UI 组件**: Ant Design Vue + VXE Table
**桌面应用**: Tauri（Rust + Web 技术栈）
**状态管理**: Pinia
**包管理**: pnpm workspaces
**测试框架**: Vitest + Vue Test Utils
**代码质量**: ESLint + Prettier
**构建工具**: Vite + Rollup
**样式处理**: Sass + Tailwind CSS
**国际化**: i18next + vue-i18n

## 🚀 快速开始

### 系统要求

- **Node.js**: >= 22
- **pnpm**: >= 9
- **Rust**: >= 1.90.0（用于 Tauri 桌面应用）
- **操作系统**: Windows 10/11、macOS 或 Linux

### 开发环境搭建

```bash
# 克隆项目
git clone https://github.com/iflytek/astron-rpa.git
cd astron-rpa/frontend

# 安装依赖
pnpm install

# 配置环境变量(必填项请参见 .env 内注释)
copy packages/web-app/.env.example packages/web-app/.env

# 启动 Web 开发服务器
pnpm dev:web

# 启动 Tauri 桌面应用（开发模式）
pnpm dev:tauri

# 启动 Tauri 日志窗口（开发模式）
pnpm dev:tauri-logwin
```

### 构建与部署

```bash
# 构建 Web 应用
pnpm build:web

# 构建 Tauri 桌面应用
pnpm build:tauri

# 构建 Tauri 桌面应用（调试模式）
pnpm build:tauri-debug

# 运行测试
pnpm test

# 运行测试（带 UI）
pnpm test:ui

# 代码检查和修复
pnpm lint:fix

# 生成国际化文件
pnpm i18n
```

## 📦 包结构

### 核心包

- **@rpa/web-app**: 主 Web 应用
- **@rpa/tauri-app**: 桌面应用（Tauri）
- **@rpa/tauri-app-window**: Tauri 日志窗口应用
- **@rpa/browser-plugin**: 浏览器扩展
- **@rpa/components**: 共享 UI 组件
- **@rpa/types**: TypeScript 类型定义
- **@rpa/tokens**: 设计令牌和样式
- **@rpa/locales**: 国际化资源

### 开发工具

- **ESLint 配置**: Antfu 的 ESLint 配置
- **测试框架**: 支持 UI 的 Vitest
- **国际化**: LobeHub i18n CLI
- **构建工具**: 支持多种构建模式的 Vite

## 🏗️ 架构概览

```
前端单体仓库
├── packages/
│   ├── web-app/           # Vue 3 Web 应用
│   ├── tauri-app/         # Tauri 桌面应用
│   ├── tauri-app-window/  # Tauri 日志窗口
│   ├── browser-plugin/    # 浏览器扩展
│   ├── components/        # 共享组件
│   ├── types/            # 类型定义
│   ├── tokens/           # 设计令牌
│   └── locales/          # i18n 资源
├── node_modules/         # 依赖包
├── package.json          # 根包配置
├── pnpm-workspace.yaml   # 工作区配置
└── vitest.config.ts      # 测试配置
```

### 技术栈详情

**Web 应用**

- Vue 3 与 Composition API
- TypeScript 提供类型安全
- Vite 作为构建工具
- Ant Design Vue 提供 UI 组件
- Pinia 进行状态管理
- Vue Router 处理路由导航

**桌面应用**

- Tauri 提供原生桌面功能
- Rust 后端与 Web 前端
- 原生系统集成
- 跨平台兼容性

**共享基础设施**

- pnpm workspaces 进行单体仓库管理
- 共享组件库
- 通用类型定义
- 统一的构建和测试流程
