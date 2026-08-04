<template>
  <!-- 应用外壳：克制的页头 + 路由出口 -->
  <div class="app-shell">
    <div class="app-inner">
      <header class="brand">
        <p class="brand-name">美食制作 AI 助手</p>
        <p class="brand-sub">输入菜名，生成一份照着做的清单式做法</p>
      </header>
      <main class="app-main">
        <RouterView />
      </main>
    </div>
  </div>
</template>