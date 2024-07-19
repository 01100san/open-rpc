package com.zhl.service;

import com.zhl.Hello;
import com.zhl.HelloService;
import com.zhl.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-16 9:24
 */
@Slf4j
@RpcService(version = "version1", group = "test1")
public class HelloServiceImpl implements HelloService {
    static {
        System.out.println("HelloServiceImpl被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到: {}.", hello.getMessage());
        String result = "description is " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }
}
