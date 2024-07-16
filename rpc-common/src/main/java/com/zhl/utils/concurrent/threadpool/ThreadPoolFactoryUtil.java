package com.zhl.utils.concurrent.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>
 *  创建线程池的工具类
 *
 * @author zhl
 * @since 2024-07-14 17:02
 */
@Slf4j
public class ThreadPoolFactoryUtil {

    /**
     * 通过 threadPoolPrefix 区分不同的线程池，一般一个业务一个线程池
     * key:threadPoolPrefix
     * value:ThreadPool
     */
    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    public ThreadPoolFactoryUtil() {
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadPoolPrefix) {
        CustomThreadPoolConfig customThreadPoolConfig = new CustomThreadPoolConfig();
        return createCustomThreadPoolIfAbsent(threadPoolPrefix, customThreadPoolConfig, false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadPoolPrefix, CustomThreadPoolConfig customThreadPoolConfig) {
        return createCustomThreadPoolIfAbsent(threadPoolPrefix, customThreadPoolConfig, false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadPoolPrefix, CustomThreadPoolConfig customThreadPoolConfig, Boolean daemon) {
        ExecutorService threadPool = THREAD_POOLS.computeIfAbsent(threadPoolPrefix, k -> createThreadPool(threadPoolPrefix, customThreadPoolConfig, daemon));
        // 如果 threadPool 被 shutdown 的话就重新创建一个
        if(threadPool.isShutdown() || threadPool.isTerminated()) {
            THREAD_POOLS.remove(threadPoolPrefix);
            threadPool = createThreadPool(threadPoolPrefix, customThreadPoolConfig, daemon);
            THREAD_POOLS.put(threadPoolPrefix, threadPool);
        }
        return threadPool;
    }

    public static void shutDownAllThreadPool() {
        log.info("call shutDownAllThreadPool method");
        THREAD_POOLS.entrySet().parallelStream().forEach(entry->{
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            log.info("shut down thread pool：[{}] [{}]", entry.getKey(), executorService.isTerminated());
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Thread pool never terminated");
                executorService.shutdownNow();
            }
        });
    }

    private static ExecutorService createThreadPool(String threadPoolPrefix, CustomThreadPoolConfig customThreadPoolConfig, Boolean daemon) {
        ThreadFactory threadFactory = createThreadFactory(threadPoolPrefix, daemon);
        return new ThreadPoolExecutor(customThreadPoolConfig.getCorePoolSize(), customThreadPoolConfig.getMaximumPoolSize(),
            customThreadPoolConfig.getKeepAliveTime(), customThreadPoolConfig.getUnit(), customThreadPoolConfig.getWorkQueue(),
            threadFactory);
    }

    private static ThreadFactory createThreadFactory(String threadPoolPrefix, Boolean daemon) {
        if(threadPoolPrefix!=null) {
            if(daemon!=null) {
                return new ThreadFactoryBuilder()
                    .setNameFormat(threadPoolPrefix + "-%d")
                    .setDaemon(daemon)
                    .build();
            }else {
                return new ThreadFactoryBuilder().setNameFormat(threadPoolPrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }



}
