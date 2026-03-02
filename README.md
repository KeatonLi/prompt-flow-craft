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
- **灵活扩展**：支持自定义工作流与模板

---

## ✨ 核心特性

### 🧙 智能向导模式
多轮问答引导，渐进式构建专业提示词：
- 任务类型识别与定义
- 目标受众分析
- 输出格式规范
- 风格与详细程度配置
- 生成结果一键复用

### 🎯 系统化提示词生成
基于**四层架构模型**的智能生成引擎：
- **核心定义层**：角色建模、目标定义
- **交互接口层**：输入规范、输出格式
- **内部处理层**：工作流程、决策逻辑
- **全局约束层**：安全边界、性能优化

### 💾 高效缓存系统
- 相同请求参数自动缓存，节省 API 成本
- 哈希算法精确匹配
- 缓存命中率实时监控

### 🏷️ 智能分类体系
- 混合分类策略：规则匹配 + AI 智能分类
- 8 大预设分类
- 自动关键词提取与标签生成
- 支持手动分类管理

### 👥 团队协作功能
- 提示词收藏与分组
- 一键分享与复用
- 使用统计与效果追踪

---

## 🛠️ 技术架构

### 后端技术栈
| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17+ | 开发语言 |
| Spring Boot | 3.2.0 | 框架 |
| Hibernate | 6.4.0 | ORM |
| MySQL | 8.0+ | 数据库 |
| Maven | 3.8+ | 构建 |

### 前端技术栈
| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.4+ | 框架 |
| TypeScript | 5.0+ | 类型安全 |
| Vite | 5.0+ | 构建工具 |
| Pinia | 2.1+ | 状态管理 |

### 系统架构图
```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend (Vue 3)                       │
├─────────────────────────────────────────────────────────────┤
│  Home │ Templates │ About  │  History Panel │ Wizard Mode │
└────────────────────────┬────────────────────────────────────┘
                         │ REST API
┌────────────────────────▼────────────────────────────────────┐
│                   Backend (Spring Boot)                     │
├──────────────┬──────────────┬──────────────┬─────────────┤
│ Prompt Gen   │  History     │ Category      │  User       │
│ Service      │  Service     │ Service       │  Service    │
└──────┬───────┴──────┬───────┴───────┬───────┴──────┬──────┘
       │              │              │              │
┌──────▼──────┐ ┌─────▼─────┐ ┌────▼──────┐ ┌───▼──────┐
│  AI Model   │ │  Cache    │ │  MySQL    │ │ External │
│  (MiniMax)  │ │ (Hibernate)│ │ Database  │ │   APIs   │
└─────────────┘ └───────────┘ └───────────┘ └──────────┘
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
cd src/main/java/com/promptflow
mvn clean package

# 4. 启动后端
java -jar target/prompt-flow-craft-1.0.0.jar

# 5. 构建前端
cd frontend
npm install
npm run build

# 6. 部署
# 将 dist 目录部署到 Nginx 或其他静态服务器
```

### Docker 部署

```bash
# 快速启动（需配置 MySQL）
docker run -d -p 8080:8080 prompt-flow-craft
```

---

## 📖 使用指南

### 简单模式
直接输入任务描述，选择参数，一键生成提示词。

### 向导模式
1. 点击「向导模式」进入
2. 回答 5 个问题
3. 获取专业级提示词
4. 一键复制或使用

### 提示词大全
- 查看所有历史生成的提示词
- 按分类筛选
- 点赞、收藏、复用

---

## 📊 API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/prompt/generate` | POST | 生成提示词 |
| `/api/history/recent` | GET | 获取最近记录 |
| `/{id}/like` | POST | 点赞 |
| `/api/category/all` | GET | 获取分类 |

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
