package com.zhl.annotation;

import java.lang.annotation.*;

/**
 * <p>
 *  消费服务
 * @author zhl
 * @since 2024-07-19 12:51
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcReference {
    /**
     * 服务的版本，默认是空字符串
     */
    String version() default "";

    /**
     * 服务的实现组，默认是空字符串
     */
    String group() default "";
}
