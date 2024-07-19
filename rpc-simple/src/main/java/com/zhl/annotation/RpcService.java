package com.zhl.annotation;

import java.lang.annotation.*;

/**
 * <p>
 *  服务注册
 * @author zhl
 * @since 2024-07-19 12:52
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {
    /**
     * 服务的版本，默认是空字符串
     */
    String version() default "";

    /**
     * 服务的实现组，默认是空字符串
     */
    String group() default "";
}
