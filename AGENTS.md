# AGENTS.md

## 项目
- 技术栈：后端 Java 17 + Spring Boot 3.4.5 + Maven；前端 Vue 3 + Vite 6 + TypeScript（vue-router / axios）；AI 用 LangChain4j + 本地 Ollama（qwen2.5:3b）
- 关键目录：
  - `backend/src/main/java/com/example/foodai/`：后端源码（`ai` 生成层 / `common` 统一响应与异常 / `config` 配置 / `recipe` 业务）
  - `frontend/src/`：前端源码（`api` 请求层 / `components` 组件 / `router` / `types` / `views` 页面）
  - `backend/src/main/resources/application.yml`：端口、AI provider、模型与 base-url 配置

## 常用命令
- 安装依赖：后端无需单独安装（首次 `mvn` 构建自动从本地 Maven 仓库解析依赖）；前端 `cd frontend && npm install`
- 运行项目：后端 `cd backend && mvn spring-boot:run`（需先 `ollama serve` 并已 `ollama pull qwen2.5:3b`）；前端 `cd frontend && npm run dev`
- 跑测试：暂无用例 —— 后端 `cd backend && mvn test`（0 用例）；前端尚未配置测试框架（建议后续引入 Vitest）
- 跑 lint：前端 `cd frontend && npx vue-tsc --noEmit`（`npm run build` 已内置）；后端暂无 lint 工具，以 `mvn compile` 编译通过为准

## 约定
- 完成标准：改动必须通过测试 + lint 才能算完成（当前落地为：后端 `mvn compile` 编译通过 + 前端 `vue-tsc --noEmit` 类型检查通过；新功能需补测试用例）
- 禁止事项：
  - 禁止提交构建产物/依赖缓存：`backend/target/`、`frontend/dist/`、`frontend/node_modules/`、`.m2/`、`logs/`、`.idea/`
  - 禁止把真实密钥/token 写进会提交到仓库的文件（含 `application.yml`）；密钥走环境变量或本地未跟踪文件（当前 Ollama 无需 api-key）
  - 禁止引入非 UTF-8 编码源文件（避免 GBK 中文乱码）
  - 禁止绕过统一响应 `ApiResponse<T>` 与 `GlobalExceptionHandler`；新异常需注册错误码
  - 禁止 `force push` 覆盖 `main` 远程历史（确需覆盖先与负责人确认）；提交前保持工作区干净

## 代码审查规则
- 必查项：
  - 响应是否走统一 `ApiResponse`，错误码是否符合约定（4000/4001/4002/4003/4004/4040/5000/5001）
  - 资源不存在是否抛 `ResourceNotFoundException`（404），而非返回示例/假数据
  - 是否补充/更新测试；前端是否通过 `vue-tsc --noEmit`
  - 是否有硬编码端口/URL/密钥；前端请求是否经 `http.ts` 拦截器统一检查 `code`
  - 内存存储与并发是否安全（`ConcurrentHashMap`、ID 生成）
  - AI 生成链路是否包含失败重试与结构校验（`OllamaDishGuideGenerator` 的 isValid/normalize）
  - 命名是否与提供商一致（Ollama 相关代码，避免再用 langchain4j 命名）
  - 是否遵守 `.gitignore`，无构建产物入库