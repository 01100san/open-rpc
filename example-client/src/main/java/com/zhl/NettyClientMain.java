package com.zhl;

import com.zhl.annotation.RpcScan;
import com.zhl.config.RpcServiceConfig;
import com.zhl.proxy.RpcClientProxy;
import com.zhl.remoting.transport.RpcRequestTransport;
import com.zhl.remoting.transport.netty.client.NettyRpcClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-18 17:17
 */
@RpcScan(basePackage = "com.zhl")
public class NettyClientMain {
    public static void main(String[] args) {
        byAnnotation();
    }

    private static void byAnnotation() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController) ctx.getBean("helloController");
        helloController.test();
    }

    private static void noAnnotation() {
        RpcRequestTransport rpcRequestTransport = new NettyRpcClient();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
            .version("version1")
            .group("test1")
            .build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceConfig);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println("hello = " + hello);
    }
}
