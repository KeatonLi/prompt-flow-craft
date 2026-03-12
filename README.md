# 🚀 Prompt Flow Craft

企业级 AI 提示词工程平台 | 多模态提示词生成与管理解决方案

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.0+-4FC08D.svg)](https://vuejs.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![GitHub Stars](https://img.shields.io/github/stars/KeatonLi/prompt-flow-craft?style=flat)](https://github.com/KeatonLi/prompt-flow-craft/stargazers)

---

## 📌 项目简介

Prompt Flow Craft 是一款面向企业的专业提示词工程平台，旨在帮助用户快速构建、优化和管理高质量的 AI 提示词。通过系统化的方法论和智能化的工具，显著提升 AI 输出的准确性和效率。

### 核心价值

- **降本增效**：智能缓存机制减少重复 API 调用
- **质量保证**：多轮优化生成高质量提示词
- **便捷管理**：完整的历史记录与分类系统
- **灵活扩展**：支持模板复用与变体生成

---

## ✨ 核心功能

### 🎯 智能提示词生成

基于**四层架构模型**的智能生成引擎：

- **核心定义层**：角色建模、目标定义
- **交互接口层**：输入规范、输出格式
- **内部处理层**：工作流程、决策逻辑
- **全局约束层**：安全边界、性能优化

**多维度参数配置**：

- 任务描述（必填）
- 目标受众：通用、专业、学生、开发者、创作者等
- 输出格式：文本、列表、表格、代码等
- 语调风格：专业、友好、简洁、创意等
- 内容长度：短篇、中篇、长篇

### 💾 高效缓存系统

- 相同请求参数自动缓存，哈希算法精确匹配
- 显著降低 API 调用成本
- 缓存命中率实时监控

### 🏷️ 智能分类体系

- **8 大预设分类**：办公效率、内容创作、代码开发、教育培训、视频媒体、数据分析、设计创意、智能对话
- 混合分类策略：规则匹配 + AI 智能分类
- 自动关键词提取与标签生成
- 支持手动分类管理

### 📚 提示词管理

- 永久保存所有提示词历史
- 支持搜索、筛选、收藏
- 一键复制与复用
- 使用统计与效果追踪

### 📦 模板市场

- 精选专业模板，一键快速生成
- 分类浏览，便捷筛选
- 模板预览与详情查看

### 🎨 变体生成

- 基于现有提示词生成多个变体
- 对比视图，便于选择最优版本
- 优化建议提示

### 📊 数据统计

- 今日/本周/本月生成数量
- 分类统计与趋势分析
- 最热提示词排行

---

## 🛠️ 技术架构

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17+ | 开发语言 |
| Spring Boot | 3.2.0 | 框架 |
| Hibernate | 6.4 | ORM |
| MySQL | 8.0+ | 数据库 |

### 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.4+ | 框架 |
| TypeScript | 5.0+ | 类型安全 |
| Vite | 5.0+ | 构建工具 |
| Pinia | 2.1+ | 状态管理 |

### 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend (Vue 3)                       │
├─────────────────────────────────────────────────────────────┤
│  Home │ Generate │ Templates │ Market │ Variants │ Stats   │
└────────────────────────┬────────────────────────────────────┘
                         │ REST API
┌────────────────────────▼────────────────────────────────────┐
│                   Backend (Spring Boot)                     │
├──────────────┬──────────────┬──────────────┬─────────────┤
│ Prompt Gen   │  History     │ Category      │ Statistics  │
│ Service      │  Service     │ Service       │ Service     │
└──────┬───────┴──────┬───────┴───────┴──────┬──────┘
       │              │                      │
┌──────▼──────┐ ┌─────▼─────┐ ┌────────────▼─────┐
│  AI Model   │ │  Cache    │ │     MySQL        │
│  (MiniMax)  │ │ (Hibernate)│ │   Database      │
└─────────────┘ └───────────┘ └──────────────────┘
```

---

## 🚀 快速开始

### 环境要求

- Node.js 18+
- Java 17+
- MySQL 8.0+
- Maven 3.8+

### 安装部署

```bash
# 1. 克隆项目
git clone https://github.com/KeatonLi/prompt-flow-craft.git
cd prompt-flow-craft

# 2. 配置数据库
# 修改 src/main/resources/application.yml 中的数据库配置

# 3. 构建后端
mvn clean package -DskipTests

# 4. 启动后端
java -jar target/prompt-flow-craft-1.0.0.jar

# 5. 构建前端
cd frontend
npm install
npm run build

# 6. 部署
# 将 dist 目录部署到 Nginx 或其他静态服务器
```

---

## 📖 功能页面

| 页面 | 路径 | 说明 |
|------|------|------|
| 首页 | `/` | 产品介绍与导航 |
| 提示词生成 | `/generate` | 创建新提示词 |
| 提示词大全 | `/templates` | 历史记录管理 |
| 模板市场 | `/template-market` | 精选模板 |
| 变体生成 | `/variants` | 提示词变体 |
| 热门提示词 | `/popular` | 热门推荐 |
| 统计 | `/statistics` | 数据统计 |

---

## 📊 API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/prompt/generate` | POST | 生成提示词 |
| `/api/history` | GET | 获取历史记录 |
| `/api/history/{id}/like` | POST | 点赞 |
| `/api/categories` | GET | 获取分类 |
| `/api/statistics` | GET | 获取统计数据 |

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/xxx`)
3. 提交更改 (`git commit -m 'Add xxx'`)
4. 推送到分支 (`git push origin feature/xxx`)
5. 创建 Pull Request

---

## 📄 开源协议

MIT License - 查看 [LICENSE](LICENSE) 了解详情

---

## 🔗 相关链接

- [项目演示](http://111.231.107.210:3000)
- [后端 API](http://111.231.107.210:8080)
- [问题反馈](https://github.com/KeatonLi/prompt-flow-craft/issues)

---

<p align="center">
  <sub>Built with ❤️ by <a href="https://github.com/KeatonLi">KeatonLi</a></sub>
</p>
