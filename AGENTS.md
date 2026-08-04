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
- 构建前端：`cd frontend && npm run build`（内含 `vue-tsc --noEmit` + `vite build`）

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
  - 响应是否走统一 `ApiResponse`，错误码是否符合约定（4000/4001/4002/4003/4004/4010/4040/4290/5000/5001）
  - 资源不存在是否抛 `ResourceNotFoundException`（404），而非返回示例/假数据
  - 鉴权/限流是否按配置开关生效（api-key 为空不鉴权；限流仅作用于生成接口）
  - 是否补充/更新测试；前端是否通过 `vue-tsc --noEmit`
  - 是否有硬编码端口/URL/密钥；前端请求是否经 `http.ts` 拦截器统一检查 `code`
  - 内存存储与并发是否安全（`ConcurrentHashMap`、ID 生成）
  - AI 生成链路是否包含失败重试与结构校验（`OllamaDishGuideGenerator` 的 isValid/normalize）
  - 命名是否与提供商一致（Ollama 相关代码，避免再用 langchain4j 命名）
  - 是否遵守 `.gitignore`，无构建产物入库

## 功能特性（现状）
- 核心闭环：输入菜名 → 后端调用本地 Ollama（qwen2.5:3b）生成结构化菜谱 → 前端详情页展示
- 生成后详情页支持：食材勾选"已备齐"（实时计数）、步骤标记"已完成"（进度条）、完成一步自动启动该步骤时长倒计时、归零弹窗提醒
- 可选 API Key 鉴权（`foodai.security.api-key`，留空不鉴权）与生成接口 IP 限流（`foodai.rate-limit.requests-per-minute`，默认 10）
- 前端通过 Vite 代理访问后端：浏览器请求 `/api/*`（同源），由 `vite.config.ts` 的 `server.proxy` 转发到 `http://localhost:8080`，局域网设备也可用

## 关键链路与数据流
```
前端提交 → POST /api/recipes/dish-guide（经 Vite 代理）
  → RecipeController → RecipeServiceImpl（分配自增 id、存内存 ConcurrentHashMap）
    → DishGuideAiService → DishGuideGenerator 接口
      → OllamaDishGuideGenerator（@Primary；provider=ollama 时生效）
        → OllamaChatModel → qwen2.5:3b → JSON → Jackson 解析 → 校验 → 失败重试一次 → 时长兜底
  → 返回 RecipeDetailResponse；前端跳转 /recipes/{id} 再 GET 渲染
```
- 数据仅存内存：后端重启即丢，查旧 id 返回 404（`ResourceNotFoundException`）
- provider 切换：`application.yml` 中 `foodai.ai.provider` 取值 `stub`（本地示例）/ `ollama`（真实模型），改后需重启后端

## 接口与错误码（详见 README.md）
- `POST /api/recipes/dish-guide`：body 含 dishName（必填，≤50 字符）、servings（1~10）、difficulty、flavor、additionalNote（均限长）
- `GET /api/recipes/{recipeId}`：不存在返回 404
- 统一响应 `ApiResponse<T>`（code/message/data），code=0 成功；错误码：4000 参数校验 / 4001 约束 / 4002 坏 JSON / 4003 参数类型 / 4004 媒体类型(415) / 4010 未授权 / 4040 资源不存在 / 4290 限流 / 5000 未知 / 5001 业务异常

## 验证方式（暂无自动化测试时的落地）
- 后端改动：`cd backend && mvn compile`；接口冒烟可用 `curl`/`Invoke-RestMethod` 打 `http://localhost:8080/api/...`（或经 `http://localhost:5173/api/...` 走代理验证全链路）
- 前端改动：`cd frontend && npx vue-tsc --noEmit && npm run build`；页面交互可用无头浏览器（CDP/Edge headless）实测，重点场景：空菜名提示、生成中禁用按钮、勾选食材/步骤计数、步骤倒计时暂停/归零弹窗
- 改 `vite.config.ts` 或后端代码后需重启对应服务；改普通前端组件 HMR 即时生效，但用户已打开的旧标签页需强刷（Ctrl+F5）

## 常见陷阱
- 前端 baseURL 是相对路径 `/api`：不要改回硬编码 `http://localhost:8080/api`，否则局域网设备无法使用
- Vite 需保持 `host: true`：只监听 IPv6 会导致浏览器（IPv4 解析 localhost）连不上
- 输入有长度校验（`@Size`），超限返回 4000；生成接口限流 10 次/分钟，连点易触发 429
- AI 输出为 JSON，`OllamaDishGuideGenerator` 负责解析/校验/重试/兜底；改 Prompt 或模型时注意保持 JSON schema 一致
- 控制台/终端看到的中文乱码通常是显示编码问题，源文件是 UTF-8 正常编码，勿因此"修复"源文件