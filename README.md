# 美食 AI 助手（Food AI Assistant）

输入一道菜名，返回结构化菜谱（标题、描述、份量、时间、难度、食材、步骤、提示），并在前端以详情页形式展示。

当前 AI 链路：前端提交菜名 -> 后端调用本地 Ollama 模型（qwen2.5:3b）生成结构化 JSON -> 解析校验后返回前端渲染。

## 技术栈

| 端 | 技术 |
|---|---|
| 后端 `backend/` | Java 17、Spring Boot 3.4.5、Maven、LangChain4j + langchain4j-ollama |
| 前端 `frontend/` | Vue 3、Vite 6、TypeScript、Vue Router、Axios |
| 本地 AI | Ollama 服务（默认 http://localhost:11434，模型 qwen2.5:3b） |

## 目录结构

```
cctest/
├── backend/                # Spring Boot API
│   └── src/main/java/com/example/foodai/
│       ├── ai/             # AI 生成层（模型/提示词/生成器）
│       ├── common/         # 统一响应、业务异常、全局异常处理
│       ├── config/         # 配置绑定、CORS
│       └── recipe/         # 菜谱业务（controller/service/vo）
├── frontend/               # Vue 3 + Vite 应用
│   └── src/
│       ├── api/            # axios 实例与接口封装
│       ├── components/     # 表单/菜谱头部/食材/步骤组件
│       ├── router/         # 路由
│       ├── types/          # 与后端对应的 TS 类型
│       └── views/          # 首页 / 菜谱详情页
└── AGENTS.md               # 面向 AI 代理的项目引导
```

## 环境要求

- JDK 17
- Maven 3.9+
- Node.js 18+（建议 20+）
- Ollama（本地模型服务）

## 快速开始

### 1. 准备 Ollama

```bash
# 启动 Ollama 服务
ollama serve

# 拉取模型（首次需要）
ollama pull qwen2.5:3b

# 确认模型已就绪
ollama list
```

### 2. 启动后端（端口 8080）

```bash
cd backend
mvn spring-boot:run
```

### 3. 启动前端（端口 5173）

```bash
cd frontend
npm install
npm run dev
```

浏览器打开 **http://localhost:5173**，输入菜名即可生成菜谱。

## 配置说明

### AI 提供商切换

编辑 `backend/src/main/resources/application.yml`：

```yaml
foodai:
  cors:
    allowed-origins: http://localhost:5173   # 跨域来源白名单（逗号分隔可配多个）
  ai:
    provider: ollama                          # stub | ollama
    model-name: qwen2.5:3b                    # Ollama 模型名
    api-key: ""                               # Ollama 无需密钥
    base-url: http://localhost:11434          # Ollama 服务地址
```

- `provider: stub` —— 本地示例生成器，不调用模型，用于纯前端演示
- `provider: ollama` —— 调用本地 Ollama 真实模型（默认配置）

### 前端接口地址

前端默认请求 `http://localhost:8080/api`，可通过环境变量覆盖（部署时指向真实后端）：

```bash
# frontend 目录下
$env:VITE_API_BASE="http://your-backend:8080/api"   # Windows PowerShell
# 或写入 frontend/.env.local
```

## API

### 生成菜谱

`POST /api/recipes/dish-guide`

请求体：

```json
{
  "dishName": "红烧肉",
  "servings": 3,
  "difficulty": "INTERMEDIATE",
  "flavor": "家常",
  "additionalNote": "少油，偏甜"
}
```

校验规则：`dishName` 必填且 ≤50 字符；`servings` 1~10；`difficulty` ≤20；`flavor` ≤50；`additionalNote` ≤200。

### 查询菜谱详情

`GET /api/recipes/{recipeId}` —— 菜谱不存在时返回 404。

### 统一响应

所有接口返回统一结构：

```json
{
  "code": 0,
  "message": "success",
  "data": { ... }
}
```

`code=0` 表示成功。错误码约定：

| code | 含义 | HTTP |
|---|---|---|
| 4000 | 参数校验失败 | 400 |
| 4001 | 约束违规 | 400 |
| 4002 | 请求体 JSON 非法 | 400 |
| 4003 | 路径参数类型错误 | 400 |
| 4004 | 媒体类型不支持 | 415 |
| 4040 | 资源不存在 | 404 |
| 5000 | 未知异常 | 500 |
| 5001 | 业务异常（如 AI 未配置） | 500 |

## 已知限制（MVP）

- **无数据库**：菜谱保存在内存中，后端重启即丢失
- **无鉴权/无限流**：接口未做身份校验与访问频率限制，仅适合本地开发
- **无自动化测试**：后端与前端均未配置测试用例