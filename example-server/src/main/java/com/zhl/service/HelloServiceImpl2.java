package com.zhl.service;

import com.zhl.Hello;
import com.zhl.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-16 9:31
 */
@Slf4j
public class HelloServiceImpl2 implements HelloService {
    static {
        System.out.println("HelloServiceImpl2被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl2收到: {}.", hello.getMessage());
        String result = "description is " + hello.getDescription();
        log.info("HelloServiceImpl2返回: {}.", result);
        return result;
    }
}
