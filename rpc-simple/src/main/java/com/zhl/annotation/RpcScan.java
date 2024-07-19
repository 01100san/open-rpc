package com.zhl.annotation;

import com.zhl.spring.CustomScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <p>
 *  扫描特定的服务，发现指定的注解 @RpcService  @RpcReference
 * @author zhl
 * @since 2024-07-19 12:56
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomScannerRegistrar.class)
@Documented
public @interface RpcScan {
    String[] basePackage() default {};
}
