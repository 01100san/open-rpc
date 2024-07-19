package com.zhl.service;

import com.zhl.Cat;
import com.zhl.HelloCat;
import com.zhl.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-19 21:20
 */
@Slf4j
@RpcService(version = "cv2", group = "ct2")
public class HelloCatServiceImpl2 implements HelloCat {

    static {
        System.out.println("HelloCatServiceImpl2被创建");
    }

    @Override
    public String helloCat(Cat cat) {
        log.info("HelloCatServiceImpl2.helloCat receive msg");
        String name = cat.getName();
        log.info(" cat name: {}", name);
        return cat.toString();
    }
}
