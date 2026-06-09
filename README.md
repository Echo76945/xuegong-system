学工系统 (Xuegong System)

一个基于 Spring Boot + Vue 3 的学生工作管理系统（学工系统），支持前后端分离部署。

---

项目简介

xuegong-system 是专为高校或教育机构设计的学生工作管理系统，包含完整的**前端、后端和数据库**解决方案。

-前端：采用 Vue 3 + Vite 构建，提供现代化的桌面端管理界面。
-后端：基于 Spring Boot 2.7 开发，集成 JPA、MyBatis、Redis 等常用框架。
-数据库：支持 MySQL（生产环境）和 H2（开发环境）。
-AI 集成：集成智谱 AI（GLM）和 OpenAI，支持 LangChain4j 实现智能对话、RAG 知识库等 AI 功能。

---

技术栈

后端 (backend)
-框架：Spring Boot 2.7.18
-数据库：MySQL / H2
-持久层：Spring Data JPA + MyBatis
-缓存：Redis
-AI 框架：LangChain4j（支持智谱 AI + OpenAI）
-其他：Lombok、Validation、Spring Cache

前端 (front)
-框架：Vue 3 + Vite
-构建工具：npm / pnpm
-UI 基础：已包含登录页、首页、基础布局（顶部导航 + 侧边栏 + 内容区）

---

项目结构
xuegong-system/
├── backend/    Spring Boot         后端
│   ├── src/main/java/com/xuegong/ 核心代码
│   ├── src/main/resources/        配置文件
│   └── pom.xml
├── front/       Vue 3              前端
│   ├── src/
│   ├── public/
│   └── package.json
├── .gitignore
└── README.md

主要功能（当前状态）
用户登录与基础权限布局
数据库实体与 JPA 映射
AI 对话接口（智谱 GLM / OpenAI）
Redis 缓存支持
正在开发中：学生信息管理、奖惩记录、活动管理、统计报表等

贡献与联系
欢迎提交 Issue 或 Pull Request！
作者：Echo76945