<template>
  <!-- 应用外壳：装饰背景 + 顶部标题 + 路由出口 -->
  <div class="app-shell">
    <div class="background-orb orb-one"></div>
    <div class="background-orb orb-two"></div>
    <header class="app-header">
      <div>
        <p class="eyebrow">Food AI Assistant</p>
        <h1>美食制作 AI 助手</h1>
        <p class="hero-note">从一道菜开始，把做法拆成清晰、可执行的步骤。</p>
      </div>
      <div class="header-copy">
        <p>输入一道菜名，直接获取结构化做法。</p>
        <p>先把最核心的体验打磨到顺手、可信、好看。</p>
      </div>
    </header>
    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>
