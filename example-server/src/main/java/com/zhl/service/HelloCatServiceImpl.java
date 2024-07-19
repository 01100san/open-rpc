package com.zhl.service;

import com.zhl.Cat;
import com.zhl.HelloCat;
import com.zhl.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-19 21:17
 */
@Slf4j
@RpcService(version = "cv1", group = "ct1")
public class HelloCatServiceImpl implements HelloCat {

    static {
        System.out.println("HelloCatServiceImpl被创建");
    }

    @Override
    public String helloCat(Cat cat) {
        log.info("HelloCatServiceImpl.helloCat receive msg");
        String name = cat.getName();
        log.info("cat name: {}", name);
        return cat.toString();
    }
}
