package com.zhl;

import com.zhl.annotation.RpcReference;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  供 客户端测试
 * @author zhl
 * @since 2024- 07-19 19:10
 */
@Component
public class HelloController {

    @RpcReference(version = "version1", group = "test1")
    private HelloService helloService;

    @RpcReference(version = "cv1", group = "ct1")
    private HelloCat helloCat;

    public void test() {
        for (int i = 0; i < 10; i++) {
            System.out.println(this.helloService.hello(new Hello("111", "222")));
        }
        for (int i = 0; i < 5; i++) {
            System.out.println(this.helloCat.helloCat(new Cat(55, "black")));
        }
        for (int i = 0; i < 5; i++) {
            System.out.println(this.helloCat.helloCat(new Cat(66, "white")));
        }
    }

}
