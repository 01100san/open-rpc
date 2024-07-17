package com.zhl.extensions;

/**
 * <p>
 *  用于保存值的辅助类
 * @author zhl
 * @since 2024-07-16 17:31
 */
public class Holder<T> {
    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
