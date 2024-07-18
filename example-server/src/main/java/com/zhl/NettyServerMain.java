package com.zhl;

import com.zhl.config.RpcServiceConfig;
import com.zhl.remoting.transport.netty.server.NettyRpcServer;
import com.zhl.service.HelloServiceImpl;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-18 17:18
 */
public class NettyServerMain {
    public static void main(String[] args) {
        NettyRpcServer nettyRpcServer = new NettyRpcServer();
        HelloService service = new HelloServiceImpl();
        nettyRpcServer.registerService(new RpcServiceConfig("version1", "test1", service));
        nettyRpcServer.start();
    }
}
