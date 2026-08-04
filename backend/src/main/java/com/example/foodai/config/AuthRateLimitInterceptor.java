package com.example.foodai.config;

import com.example.foodai.common.RateLimitExceededException;
import com.example.foodai.common.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 鉴权 + 限流拦截器（作用于 /api/**）。
 * <p>
 * 鉴权：配置了 foodai.security.api-key 后，请求必须携带 X-API-Key 请求头且值一致，否则抛 401；
 * api-key 留空则跳过（本地开发默认）。
 * <p>
 * 限流：对"生成菜谱"接口（POST /api/recipes/dish-guide）按客户端 IP 做 60 秒滑动窗口计数，
 * 超限抛 429，防止被刷接口消耗 AI 算力。
 * <p>
 * 注意：计数保存在内存中，进程重启清零；多实例部署需替换为 Redis 等分布式方案。
 */
@Component
public class AuthRateLimitInterceptor implements HandlerInterceptor {

    private final ApiSecurityProperties securityProperties;
    private final RateLimitProperties rateLimitProperties;

    /** 每个 IP 的请求时间戳队列（滑动窗口计数用） */
    private final Map<String, Deque<Long>> requestLog = new ConcurrentHashMap<>();

    /** 超过该条数后触发一次惰性清理，防止 Map 无限增长（内存泄漏） */
    private static final int MAX_ENTRIES = 10_000;

    /** 上次清理时间（CAS 保证并发下只清理一次） */
    private final AtomicLong lastPruneAt = new AtomicLong(0L);

    public AuthRateLimitInterceptor(ApiSecurityProperties securityProperties,
                                    RateLimitProperties rateLimitProperties) {
        this.securityProperties = securityProperties;
        this.rateLimitProperties = rateLimitProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 浏览器跨域预检请求不会携带自定义请求头，直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // —— 鉴权：仅当配置了 api-key 时启用 ——
        if (StringUtils.hasText(securityProperties.getApiKey())) {
            String provided = request.getHeader("X-API-Key");
            if (!securityProperties.getApiKey().equals(provided)) {
                throw new UnauthorizedException("Missing or invalid X-API-Key");
            }
        }

        // —— 限流：只针对计算昂贵的 AI 生成接口 ——
        if ("POST".equalsIgnoreCase(request.getMethod())
                && request.getRequestURI().endsWith("/api/recipes/dish-guide")) {
            checkRateLimit(request.getRemoteAddr());
        }
        return true;
    }

    /**
     * 滑动窗口限流：清理窗口外的旧记录后，若计数达到阈值则拒绝，否则记录当前时间。
     */
    private void checkRateLimit(String clientIp) {
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

    /**
     * 惰性清理：当 IP 条目数超过阈值时，每 60 秒移除一次"窗口内无请求记录"的条目，
     * 避免长期运行后 requestLog 无限膨胀。
     */
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
