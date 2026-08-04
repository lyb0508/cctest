package com.example.foodai.config;

import com.example.foodai.common.RateLimitExceededException;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 生成菜谱接口的 IP 级滑动窗口限流。
 * 由 RecipeController 在 @Valid 校验通过后调用，因此坏请求不会消耗配额。
 * 内存实现：进程重启清零；多实例部署需替换为 Redis 等分布式方案。
 */
@Component
public class RateLimiter {

    private final RateLimitProperties rateLimitProperties;
    private final Map<String, Deque<Long>> requestLog = new ConcurrentHashMap<>();

    /** 超过该条数后触发一次惰性清理，防止 Map 无限增长 */
    private static final int MAX_ENTRIES = 10_000;

    /** 上次清理时间（CAS 保证并发下只清理一次） */
    private final AtomicLong lastPruneAt = new AtomicLong(0L);

    public RateLimiter(RateLimitProperties rateLimitProperties) {
        this.rateLimitProperties = rateLimitProperties;
    }

    /** 尝试获取一次配额，超限抛 RateLimitExceededException（全局处理为 429） */
    public void acquire(String clientIp) {
        long now = System.currentTimeMillis();
        long windowMillis = 60_000L;
        Deque<Long> deque = requestLog.computeIfAbsent(clientIp, k -> new ArrayDeque<>());
        synchronized (deque) {
            while (!deque.isEmpty() && now - deque.peekFirst() > windowMillis) {
                deque.pollFirst();
            }
            if (deque.size() >= rateLimitProperties.getRequestsPerMinute()) {
                throw new RateLimitExceededException("Too many requests, please try again later");
            }
            deque.addLast(now);
        }
        pruneIfNeeded(now, windowMillis);
    }

    /** 惰性清理：条目超阈值时每 60 秒移除窗口内无请求的 IP，防止内存无限增长 */
    private void pruneIfNeeded(long now, long windowMillis) {
        if (requestLog.size() < MAX_ENTRIES) {
            return;
        }
        long last = lastPruneAt.get();
        if (now - last < windowMillis) {
            return;
        }
        if (!lastPruneAt.compareAndSet(last, now)) {
            return;
        }
        requestLog.entrySet().removeIf(entry -> {
            Deque<Long> deque = entry.getValue();
            synchronized (deque) {
                return deque.isEmpty() || now - deque.peekLast() > windowMillis;
            }
        });
    }
}