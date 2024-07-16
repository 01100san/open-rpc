package com.zhl.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *  单例池工厂
 * @author zhl
 * @since 2024-07-15 14:11
 */
public class SingletonFactory {
    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    public SingletonFactory() {
    }

    /**
     * 获取单例对象，如果没有则创建单例对象并存入 map 缓存中
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<T> clazz) {
        if(clazz==null) {
            throw new IllegalArgumentException();
        }
        if(OBJECT_MAP.containsKey(clazz.toString())) {
            return clazz.cast(OBJECT_MAP.get(clazz.toString()));
        }else {
            return clazz.cast(OBJECT_MAP.computeIfAbsent(clazz.toString(), key -> {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
    }
}
