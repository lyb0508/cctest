<script setup lang="ts">
import { computed, onUnmounted, ref } from "vue";
import type { RecipeStep } from "../types/recipe";

// 制作步骤：点击标记完成（进度条）；完成一项自动启动该步骤时长的倒计时，归零弹窗提醒
const props = defineProps<{
  steps: RecipeStep[];
}>();

// —— 完成状态 ——
const done = ref<Set<number>>(new Set());
const doneCount = computed(() => done.value.size);
const progress = computed(() =>
  props.steps.length ? Math.round((doneCount.value / props.steps.length) * 100) : 0
);

// —— 定时器：同一时间只跑一个（按步骤顺序做饭更符合实际）——
interface ActiveTimer {
  stepIndex: number;
  totalMs: number;
  endAt: number;      // 运行中时的时间戳；暂停时置 0
  remainingMs: number;
  running: boolean;
}

const activeTimer = ref<ActiveTimer | null>(null);
const timerModal = ref<{ stepIndex: number; title: string } | null>(null);
let tickTimer: number | undefined;

function clearTick() {
  if (tickTimer) {
    window.clearInterval(tickTimer);
    tickTimer = undefined;
  }
}

function startTimer(stepIndex: number) {
  const step = props.steps[stepIndex];
  const durationMin = step.duration ?? 0;
  if (durationMin <= 0) {
    return; // 无有效时长则只标记完成，不启动计时
  }
  clearTick();
  const totalMs = durationMin * 60_000;
  activeTimer.value = {
    stepIndex,
    totalMs,
    endAt: Date.now() + totalMs,
    remainingMs: totalMs,
    running: true
  };
  tickTimer = window.setInterval(tick, 500);
}

function tick() {
  const t = activeTimer.value;
  if (!t) {
    clearTick();
    return;
  }
  if (!t.running) {
    return;
  }
  t.remainingMs = Math.max(0, t.endAt - Date.now());
  if (t.remainingMs <= 0) {
    // 倒计时归零：停止计时并弹窗提醒
    clearTick();
    const title = props.steps[t.stepIndex]?.title ?? "";
    activeTimer.value = null;
    timerModal.value = { stepIndex: t.stepIndex, title };
  }
}

function pauseTimer() {
  const t = activeTimer.value;
  if (!t || !t.running) {
    return;
  }
  t.remainingMs = Math.max(0, t.endAt - Date.now());
  t.running = false;
  clearTick();
}

function resumeTimer() {
  const t = activeTimer.value;
  if (!t || t.running) {
    return;
  }
  t.endAt = Date.now() + t.remainingMs;
  t.running = true;
  clearTick();
  tickTimer = window.setInterval(tick, 500);
}

function cancelTimer() {
  clearTick();
  activeTimer.value = null;
}

// 剩余时间 mm:ss
const remainingText = computed(() => {
  const t = activeTimer.value;
  if (!t) {
    return "00:00";
  }
  const totalSec = Math.ceil(t.remainingMs / 1000);
  const m = Math.floor(totalSec / 60);
  const s = totalSec % 60;
  return `${String(m).padStart(2, "0")}:${String(s).padStart(2, "0")}`;
});

const activeStepName = computed(() => {
  const t = activeTimer.value;
  return t ? props.steps[t.stepIndex]?.title ?? "" : "";
});

function toggle(index: number) {
  const next = new Set(done.value);
  if (next.has(index)) {
    // 取消完成：若该步骤计时在跑，一并取消
    next.delete(index);
    if (activeTimer.value?.stepIndex === index) {
      cancelTimer();
    }
  } else {
    next.add(index);
    startTimer(index);
  }
  done.value = next;
}

onUnmounted(clearTick);
</script>

<template>
  <section class="card">
    <header class="card-head">
      <div>
        <p class="eyebrow">Steps</p>
        <h3>制作步骤</h3>
      </div>
      <span class="count-pill" :class="{ done: doneCount === steps.length }">
        {{ doneCount }}/{{ steps.length }} 已完成
      </span>
    </header>

    <!-- 进行中的倒计时 -->
    <div v-if="activeTimer" class="timer-panel" role="timer" aria-live="polite">
      <div class="timer-info">
        <span class="timer-label">第 {{ activeTimer.stepIndex + 1 }} 步 · {{ activeStepName }}</span>
        <strong class="timer-time">{{ remainingText }}</strong>
      </div>
      <div class="timer-actions">
        <button v-if="activeTimer.running" type="button" class="timer-btn" @click="pauseTimer">暂停</button>
        <button v-else type="button" class="timer-btn" @click="resumeTimer">继续</button>
        <button type="button" class="timer-btn ghost" @click="cancelTimer">跳过</button>
      </div>
    </div>

    <div class="progress-track" role="progressbar" :aria-valuenow="progress" aria-valuemin="0" aria-valuemax="100">
      <div class="progress-bar" :style="{ width: `${progress}%` }"></div>
    </div>

    <ol class="step-list">
      <li
        v-for="(step, i) in steps"
        :key="`${step.stepNo}-${i}`"
        class="step-item"
        :class="{ done: done.has(i) }"
        :aria-pressed="done.has(i)"
        role="button"
        tabindex="0"
        @click="toggle(i)"
        @keydown.enter.space.prevent="toggle(i)"
      >
        <span class="step-index" aria-hidden="true">{{ step.stepNo }}</span>
        <div class="step-body">
          <div class="step-meta">
            <h4 class="step-title">{{ step.title }}</h4>
            <span class="time-chip">约 {{ step.duration }} 分钟</span>
          </div>
          <p>{{ step.content }}</p>
          <p v-if="step.note" class="step-note">提示：{{ step.note }}</p>
        </div>
      </li>
    </ol>
  </section>

  <!-- 计时结束弹窗 -->
  <Teleport to="body">
    <div v-if="timerModal" class="modal-mask" @click.self="timerModal = null">
      <div class="modal" role="dialog" aria-modal="true" aria-labelledby="timer-modal-title">
        <p class="eyebrow">Timer</p>
        <h3 id="timer-modal-title" class="modal-title">时间到！</h3>
        <p class="modal-text">
          第 {{ timerModal.stepIndex + 1 }} 步「{{ timerModal.title }}」计时结束，可以进行下一步了。
        </p>
        <button type="button" class="btn-primary modal-btn" @click="timerModal = null">知道了</button>
      </div>
    </div>
  </Teleport>
</template>