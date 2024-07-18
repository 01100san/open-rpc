package com.zhl.utils.concurrent.threadpool;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-18 13:40
 */
public class RuntimeUtil {
    /**
     * 获取CPU的核心数
     * @return cpu的核心数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
