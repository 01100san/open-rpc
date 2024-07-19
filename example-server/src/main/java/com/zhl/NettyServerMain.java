package com.zhl;

import com.zhl.annotation.RpcScan;
import com.zhl.config.RpcServiceConfig;
import com.zhl.remoting.transport.netty.server.NettyRpcServer;
import com.zhl.service.HelloServiceImpl;
import com.zhl.service.HelloServiceImpl2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-18 17:18
 */
@RpcScan(basePackage = "com.zhl")
public class NettyServerMain {
    public static void main(String[] args) {
        byAnnotation();
    }

    private static void byAnnotation() {
        // 基于注解扫描注册 ===> HelloServiceImpl1
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        // 手动注册服务 ===> HelloServiceImpl2
        HelloService helloService2 = new HelloServiceImpl2();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
            .group("test2").version("version2").service(helloService2).build();
        nettyRpcServer.registerService(rpcServiceConfig);

        nettyRpcServer.start();
    }

    private static void noAnnotation() {
        NettyRpcServer nettyRpcServer = new NettyRpcServer();
        HelloService service = new HelloServiceImpl();
        nettyRpcServer.registerService(new RpcServiceConfig("version1", "test1", service));
        nettyRpcServer.start();
    }
}
