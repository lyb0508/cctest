# Food AI Assistant MVP

Current vertical slice:

- frontend input for a dish name
- backend returns a structured recipe guide
- frontend renders the recipe detail page

## Directories

- `frontend/` Vue 3 + Vite app
- `backend/` Spring Boot API

## Run

### Frontend

```bash
cd frontend
npm install
npm run dev
```

### Backend

```bash
cd backend
mvn spring-boot:run
```

## API

- `POST /api/recipes/dish-guide`
- `GET /api/recipes/{recipeId}`

## AI provider switch

Default mode uses the local stub generator.

Note: Ollama needs no `api-key`. Make sure the Ollama service is running and the model is pulled (`ollama pull qwen2.5:3b`).

To use a real local model through Ollama, set these fields in `backend/src/main/resources/application.yml`:

```yaml
foodai:
  ai:
    provider: ollama
    model-name: qwen2.5:3b
    api-key: ""
    base-url: http://localhost:11434
```

Current status:

- `provider: stub` -> local structured output
- `provider: ollama` -> calls the local Ollama service (e.g. `qwen2.5:3b`) through LangChain4j and parses the structured JSON response
